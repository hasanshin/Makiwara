package ru.i_fighter.makiwara1;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by X550 on 09.11.2016.
 */

public class PageFragment extends Fragment {
    static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";

    int pageNumber;
    //int backColor;


    static PageFragment newInstance(int page) {
        PageFragment pageFragment = new PageFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
        pageFragment.setArguments(arguments);
        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNumber = getArguments().getInt(ARGUMENT_PAGE_NUMBER);

        // Random rnd = new Random();
        // backColor = Color.argb(40, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment, null);

        /*TextView tvPage = (TextView) view.findViewById(R.id.tvPage);
        TextView txtStep2 = (TextView) view.findViewById(R.id.txtStep2);
        TextView txtStep3 = (TextView) view.findViewById(R.id.txtStep3);

        ImageView imgHandHandy = (ImageView)view.findViewById(R.id.imgInHand);
        ImageView imgPunchHandy = (ImageView)view.findViewById(R.id.imgPunch);
        ImageView imgStartHandy = (ImageView)view.findViewById(R.id.imgStep2);*/
        /*if(pageNumber==0) {
            tvPage.setText(R.string.tvPage);

            imgHandHandy.setVisibility(View.VISIBLE);
            //imgPunchHandy.setEnabled(false);
            //tvPage.setText("Page " + pageNumber);
            //tvPage.setBackgroundColor(backColor);
        }
        if(pageNumber==1) {
            txtStep2.setText(R.string.txtStep2);
            //tvPage.setText("Page " + pageNumber);

            imgStartHandy.setVisibility(View.VISIBLE);
            // imgHandHandy.setEnabled(false);
            //txtStep2.setBackgroundColor(backColor);
        }
        if(pageNumber==2) {
            txtStep2.setText(R.string.txtStep3);
            //tvPage.setText("Page " + pageNumber);

            imgPunchHandy.setVisibility(View.VISIBLE);
            // imgHandHandy.setEnabled(false);
            //txtStep2.setBackgroundColor(backColor);
        }*/

        return view;
    }

}

