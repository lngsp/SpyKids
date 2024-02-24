package ro.spykids.clientapp.ui.fragment.child;

import static android.content.Context.WINDOW_SERVICE;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import ro.spykids.clientapp.R;

public class GenerateQRFragment extends Fragment {

    private String  email, token;

    private ImageView imgQR;
    private Bitmap bitmap;
    private QRGEncoder qrgEncoder;

    public GenerateQRFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_generate_qr, container, false);

        Bundle args = getArguments();
        if (args != null) {
            email = args.getString("email");
            token = args.getString("token");
        }

        //remove the status bar
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        imgQR = view.findViewById(R.id.imgQR);

        WindowManager manager = (WindowManager) getContext().getSystemService(WINDOW_SERVICE);

        Display display = manager.getDefaultDisplay();     // default display

        Point point = new Point();  // point which is to be displayed in QR Code
        display.getSize(point);

        int width = point.x;
        int height = point.y;

        // generating dimension from width and height.
        int dimen;
        if (width < height) {
            dimen = width;
        } else {
            dimen = height;
        }

        dimen = dimen * 3 / 4;

        // generate our qr code.
        qrgEncoder = new QRGEncoder(email, null, QRGContents.Type.TEXT, dimen);
        // getting our qrcode in the form of bitmap
        bitmap = qrgEncoder.getBitmap();
        // the bitmap is set inside our image
        imgQR.setImageBitmap(bitmap);

        return view;
    }
}
