package ro.spykids.server.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.spykids.server.controller.request.AddChildToRestrictedAreaRequest;
import ro.spykids.server.controller.response.AreaResponse;
import ro.spykids.server.controller.response.IsInAreaResponse;
import ro.spykids.server.exceptions.*;
import ro.spykids.server.model.Area;
import ro.spykids.server.model.AreaType;
import ro.spykids.server.model.User;
import ro.spykids.server.pojo.Circle;
import ro.spykids.server.pojo.Line;
import ro.spykids.server.pojo.Point;
import ro.spykids.server.repository.AreaRepository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AreaService {

    private final AreaRepository areaRepository;
    private final UserService userService;
    private final EncryptionService encryptionService;

    public List<Area> findAll(){
        List<Area> list = areaRepository.findAll();
        if(list.isEmpty()){
            throw new NoAreaFoundExceptions("There is no area defined!");
        }
        return list;
    }

    public void saveArea(Area area) throws RuntimeException {
        //check if the area is already defined for the parent from request

        //encrypt the parent email who defined the area & the name of the area
        String encryptedName = encryptionService.encrypt(area.getName());
        String encryptedDefinedBy = encryptionService.encrypt(area.getDefinedBy());
        String encryptedData = encryptionService.encrypt(area.getData());

        //check if the email is a valide one
        Optional<User> userExist = userService.findByEmail(area.getDefinedBy());

        if(userExist.isPresent()){

            //check if the email is not from a child account
            if(userExist.get().getType().toString().equals("CHILD")){
                throw new IllegalArgumentException("Invalid user type (CHILD). Cannot perform this operation.");
            }

            //check if there is an area with the same name defined by the parent
            Area r = areaRepository.findByNameAndDefinedBy(encryptedName, encryptedDefinedBy);
            if(r != null){
                throw new AreaAlreadyExistsException("The area with this name is already defined!");
            }

            JSONObject jsonObject = new JSONObject(area.getData());
            int numPoints = 0;

            for (String key : jsonObject.keySet()) {
                if (key.startsWith("point") && jsonObject.get(key) instanceof JSONObject) {
                    numPoints++;
                    if (numPoints > 10) {
                        throw new TooManyPointsException("Too many points, maximum 10!");
                    }
                }
            }

            //insert the values in database encrypted
            area.setDefinedBy(encryptedDefinedBy);
            area.setName(encryptedName);
            area.setData(encryptedData);
            areaRepository.save(area);
        }
        else {
            throw new UsernameNotFoundException("The email is not asosiated with an account!");
        }

    }

    public List<AreaResponse> getParentAreas(String parentEmail){
        String encryptedDefinedBy = encryptionService.encrypt(parentEmail);

        //check if the email is a valide one
        Optional<User> userExist = userService.findByEmail(parentEmail);

        if(userExist.isPresent()){
            //check if the email is not from a child account
            if(userExist.get().getType().toString().equals("CHILD")){
                throw new IllegalArgumentException("Invalid user type (CHILD). Cannot perform this operation.");
            }

            List<Area> list = areaRepository.findByDefinedBy(encryptedDefinedBy);
            if(list.isEmpty()){
                throw new NoAreaFoundExceptions("There is no area defined!");
            }

            List<AreaResponse> areaResponseList = new ArrayList<>();
           for (Area a: list){
               AreaResponse ar = new AreaResponse();
               ar.setName(encryptionService.decrypt(a.getName()));
               ar.setData(encryptionService.decrypt(a.getData().toString()));
               ar.setAreaType(AreaType.valueOf(a.getAreaType().toString()));
               ar.setEnable(a.getEnable());
               ar.setSafe(a.getSafe());
               areaResponseList.add(ar);
           }
           return areaResponseList;
        }
        else {
            throw new UsernameNotFoundException("The email is not asosiated with an account!");
        }

    }

    public void modifySafety(String definedBy, String name, Boolean safe){
        String encryptedName = encryptionService.encrypt(name);
        String encryptedDefinedBy = encryptionService.encrypt(definedBy);

        Area r = areaRepository.findByNameAndDefinedBy(encryptedName, encryptedDefinedBy);
        if(r == null){
            throw new NoAreaFoundExceptions("The area with this name is not defined!");
        }

        areaRepository.updateSafe(encryptedDefinedBy, encryptedName, safe);
    }

    public void modifyEnable(String definedBy, String name, Boolean enable){
        String encryptedName = encryptionService.encrypt(name);
        String encryptedDefinedBy = encryptionService.encrypt(definedBy);

        Area r = areaRepository.findByNameAndDefinedBy(encryptedName, encryptedDefinedBy);
        if(r == null){
            throw new NoAreaFoundExceptions("The area with this name is not defined!");
        }

        areaRepository.updateEnable(encryptedDefinedBy, encryptedName, enable);
    }


    //GET AREA: ALL / TRIANGLE / POLYGON / CIRCLE
    public List<AreaResponse> getChildArea(String childEmail){

        User child = userService.findByEmail(childEmail)
                .orElseThrow(() -> new UsernameNotFoundException("There is no child account with this email address."));

        List<AreaResponse> list = new ArrayList<>();

        List<Area> areas = areaRepository.findAllAreaByChildId(child.getId());

        if(areas.isEmpty()){
            throw new NoAreaFoundExceptions("There is no area defined for user " + childEmail + "!");
        }

        for(Area ra : areas){
            String decryptedData = encryptionService.decrypt(ra.getData());
            String decryptedName = encryptionService.decrypt(ra.getName());

            AreaResponse response = new AreaResponse();

            response.setAreaType(ra.getAreaType());
            response.setData(decryptedData);
            response.setName(decryptedName);
            response.setEnable(ra.getEnable());
            response.setSafe(ra.getSafe());

            list.add(response);
        }

        return list;
    }

    public List<AreaResponse> getChildPolygonArea(String childEmail){

        User child = userService.findByEmail(childEmail)
                .orElseThrow(() -> new UsernameNotFoundException("There is no child account with this email address."));

        List<AreaResponse> list = new ArrayList<>();

        List<Area> areas = areaRepository.findAllPolygonAreaByChildId(child.getId());

        for(Area ra : areas){
            String decryptedData = encryptionService.decrypt(ra.getData());
            String decryptedName = encryptionService.decrypt(ra.getName());

            AreaResponse response = new AreaResponse();

            response.setAreaType(ra.getAreaType());
            response.setData(decryptedData);
            response.setName(decryptedName);
            response.setEnable(ra.getEnable());
            response.setSafe(ra.getSafe());

            list.add(response);
        }

        return list;
    }

    public List<AreaResponse> getChildCircleArea(String childEmail){

        User child = userService.findByEmail(childEmail)
                .orElseThrow(() -> new UsernameNotFoundException("There is no child account with this email address."));

        List<AreaResponse> list = new ArrayList<>();

        List<Area> areas = areaRepository.findAllCircleAreaByChildId(child.getId());

        for(Area ra : areas){
            String decryptedData = encryptionService.decrypt(ra.getData());
            String decryptedName = encryptionService.decrypt(ra.getName());

            AreaResponse response = new AreaResponse();

            response.setAreaType(ra.getAreaType());
            response.setData(decryptedData);
            response.setName(decryptedName);
            response.setEnable(ra.getEnable());
            response.setSafe(ra.getSafe());

            list.add(response);
        }

        return list;
    }
    //End get area


    //ADD a child to an restricted area
    public void addChild(AddChildToRestrictedAreaRequest request) throws DifferentParentException {
        User child = userService.findByEmail(request.getChildEmail())
                .orElseThrow(() -> new UsernameNotFoundException("There is no child account with this email address."));

        User parent = userService.findByEmail(request.getParentEmail())
                .orElseThrow(() -> new UsernameNotFoundException("There is no parent account with this email address."));

        if(parent.getType().equals("CHILD")){
            throw new UserNotAllowedException("A child cannot perform this operation!");
        }

        String encryptedAreaName = encryptionService.encrypt(request.getAreaName());
        String encryptedParentEmail = encryptionService.encrypt(request.getParentEmail());

        List<Area> areas = areaRepository.findAllAreaByChildId(child.getId());
        for(Area area : areas){
            if(area.getName().equals(encryptedAreaName)){
                throw new AreaAlreadyExistsException("The area with this name is already added to the child!");
            }
        }

        Area area = areaRepository.findByName(encryptedAreaName);

        if(areaRepository.findByNameAndDefinedBy(encryptedAreaName, encryptedParentEmail) == null){
            throw new NoAreaFoundExceptions("There is no restricted area with this name.");
        }
        if(!area.getDefinedBy().equals(encryptedParentEmail)){
            throw new DifferentParentException("You cannot add an area defined by another parent!");
        }

        area.getUserA().add(child);
        areaRepository.save(area);
    }


    //CHECK IF THE LOCATION IS IN THE AREA
    public List<IsInAreaResponse> checkLocationAllArea(String email, Double latitude, Double longitude){
        List<IsInAreaResponse> list1 = checkLocationPolygonArea(email, latitude, longitude);
        List<IsInAreaResponse> list2 = checkLocationCircleArea(email, latitude, longitude);

        List<IsInAreaResponse> result = new ArrayList<>();
        if(list1.isEmpty()==false)
            result.addAll(list1);
        if(list2.isEmpty()==false)
            result.addAll(list2);

        return result;

    }


    ///////  POLYGON
    public List<Point> getPoints(String data){
        JSONObject jsonObject = new JSONObject(data);

        List<Point> points = new ArrayList<>();

        Iterator<String> keys = jsonObject.keys();

        while (keys.hasNext()) {
            String key = keys.next();
            JSONObject pointObject = jsonObject.getJSONObject(key);
            double point_lat = pointObject.getDouble("latitude");
            double point_long = pointObject.getDouble("longitude");
            Point point = new Point(point_lat, point_long);
            points.add(point);
        }
        return points;
    }

    public List<IsInAreaResponse> checkLocationPolygonArea(String email, Double latitude, Double longitude){
        List<IsInAreaResponse> isInAreaList = new ArrayList<>();
        Point point = new Point(latitude, longitude);
        boolean check;

        //we go through the locations for each child
        List<AreaResponse> list = getChildPolygonArea(email);

        //we go through the zones one by one for the current child
        for (AreaResponse area : list) {
            if(area.getEnable()==true){
                IsInAreaResponse response = new IsInAreaResponse();
                response.setAreaName(area.getName());

                //get points of polygon
                List<Point> polygon = getPoints(area.getData());

                check = checkInsidePolygon(polygon, point);
                response.setIsInArea(check);
                response.setSafe(area.getSafe());
                isInAreaList.add(response);
            }
        }
        return isInAreaList;
    }

    private static boolean onLine(Line l, Point p){
        //check if p is on the line or not
        //a point P(x, y) is on the line define by P1(x1, y1) and P2(x2, y2) if
        //the determinant formed by the three points and the column with only 1 is 0
        // | x  y 1 |
        // |x1 y1 1 | = 0
        // |x2 y2 1 |
        // the determinant is: x * y1 + x1 * y2 + y * x2 - y1 * x2 - x * y2 - y * x1 == 0
        double x = p.getX();
        double y = p.getY();
        double x1 = l.getP1().getX();
        double y1 = l.getP1().getY();
        double x2 = l.getP2().getX();
        double y2 = l.getP2().getY();

        double determinant = x * y1 + x1 * y2 + y * x2 - y1 * x2 - x * y2 - y * x1;
        if(determinant == 0)
            return true;
        else return false;
    }

    private static int direction(Point p1, Point p2, Point p3){
        //check the direction with vector product.
        //P1(x1, y1), P2(x2, y2), P3(x3, y3)
        //P1P2 = (x2 - x1, y2 - y1)
        //P1P3 = (x3 - x1, y3 - y1)
        //P1P2 x P1P3 = = (x2 - x1, y2 - y1) x (x3 - x1, y3 - y1) = (x2 - x1) * (y3 - y1) - (x3 - x1) * (y2 - y1)
        //if == 0: collinear
        //if > 0 : clockwise
        //if < 0: trigonometric sense

        double p1p2_x = p2.getX() - p1.getX();
        double p1p2_y = p2.getY() - p1.getY();
        double p1p3_x = p3.getX() - p1.getX();
        double p1p3_y = p3.getY() - p1.getY();

        double product = p1p2_x * p1p3_y - p1p3_x * p1p2_y;
        if(product == 0)
            return 0;
        else if (product < 0)
            return -1;
        else return 1;
    }

    private static boolean isLinesIntersect(Line l1, Line l2){
        int direction_1 = direction(l1.getP1(), l1.getP2(), l2.getP1());
        int direction_2 = direction(l1.getP1(), l1.getP2(), l2.getP2());
        int direction_3 = direction(l2.getP1(), l2.getP2(), l1.getP1());
        int direction_4 = direction(l2.getP1(), l2.getP2(), l1.getP2());

        boolean onLine_1 = onLine(l1, l2.getP1());
        boolean onLine_2 = onLine(l1, l2.getP2());
        boolean onLine_3 = onLine(l2, l1.getP1());
        boolean onLine_4 = onLine(l2, l1.getP2());

        if(direction_1 != direction_2 && direction_3 != direction_4)//is intersecting
            return true;
        if(direction_1 == 0 && onLine_1)
            return true;
        if(direction_2 == 0 && onLine_2)
            return true;
        if(direction_3 == 0 && onLine_3)
            return true;
        if(direction_4 == 0 && onLine_4)
            return true;
        return false;
    }

    private static boolean checkInsidePolygon(List<Point> polygon, Point p){
        int size = polygon.size();
        if(size < 3)   //when polygon has less than 3 points, its not a polygon
            return false;

        // Create a point at infinity, y is same as point p
        Point infinity = new Point(Double.POSITIVE_INFINITY, p.getY());
        Line ray = new Line(p, infinity);
        int intersections = 0;

        for (int i = 0; i < size; i++) {

            // Form a line segment from two consecutive points of polygon
            Line segment = new Line(polygon.get(i), polygon.get((i+1)%size));

            // Check if the ray intersects the line segment
            if (isLinesIntersect(segment, ray) == true) {

                // If side is intersects exline
                if (direction(segment.getP1(), p, segment.getP2()) == 0){
                   return onLine(segment, p);
                }
                intersections++;
            }
        }
        return intersections % 2 == 1 ? true : false;
    }

    ///////  CIRCLE
    private List<IsInAreaResponse> checkLocationCircleArea(String email, Double latitude, Double longitude) {
        List<IsInAreaResponse> isInAreaList = new ArrayList<>();
        Point point = new Point(latitude, longitude);
        boolean check;

        //we go through the locations for each child
        List<AreaResponse> list = getChildCircleArea(email);

        //we go through the enable zones one by one for the current child
        for (AreaResponse area : list) {
            if(area.getEnable()==true){
                IsInAreaResponse response = new IsInAreaResponse();
                response.setAreaName(area.getName());

                //get points of polygon

                JSONObject jsonObject = new JSONObject(area.getData());
                double centerX = jsonObject.getJSONObject("center").getDouble("latitude");
                double centerY = jsonObject.getJSONObject("center").getDouble("longitude");
                double radius = jsonObject.getDouble("radius");

                Circle circle = new Circle(new Point(centerX, centerY), radius);
                check = checkInsideCircle(circle, point);
//                System.out.println("CIRCLE : " + circle.getPoint().getX() + " " + circle.getRadius() + " check: " + check);

                response.setIsInArea(check);
                response.setSafe(area.getSafe());
                isInAreaList.add(response);
            }
        }
        return isInAreaList;
    }

    private boolean checkInsideCircle(Circle circle, Point point){
        double distance = Math.sqrt((point.getX() - circle.getPoint().getX()) * (point.getX() - circle.getPoint().getX())
                + (point.getY() - circle.getPoint().getY()) * (point.getY() - circle.getPoint().getY()));

        if (distance <= circle.getRadius())
            return true;
        else return false;
    }
}
