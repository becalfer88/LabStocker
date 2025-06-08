package bcf.tfc.labstocker.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

import bcf.tfc.labstocker.R;
import bcf.tfc.labstocker.adapters.ItemFeed;
import bcf.tfc.labstocker.model.DataModel;
import bcf.tfc.labstocker.model.data.Laboratory;
import bcf.tfc.labstocker.model.data.Location;
import bcf.tfc.labstocker.model.data.Subject;
import bcf.tfc.labstocker.model.data.Warehouse;


public class Utils {

    public static boolean validateEmail(String email) {
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Creates an AlertDialog with the error message
     * @param context
     * @param message to be shown
     * @return the dialog
     */
    public static @NonNull AlertDialog getErrorDialog(Context context, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.error);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        return dialog;
    }

    public static @NonNull AlertDialog getDeleteDialog(Context context, Object item)  {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.delete);
        builder.setMessage(R.string.delete_confirmation);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (item instanceof ItemFeed) {
                    ((ItemFeed) item).delete(context);
                } else if (item instanceof Subject) {
                    DataModel.deleteSubject(context,(Subject) item);
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        return dialog;
    }


    /**
     *
     * @param view
     */
    public static void changeVisibility(View view){
        if (view.getVisibility() == View.VISIBLE) {
            view.setVisibility(View.INVISIBLE);
        } else {
            view.setVisibility(View.VISIBLE);
        }
    }

    /**
     *
     * @param views
     */
    public static void setVisibility(ArrayList<View> views){
        for (View view : views) {
            changeVisibility(view);
        }
    }

    public static void setEditable(ArrayList<View> views, boolean editable){
        for (View view : views) {
            if (!(view instanceof CompoundButton)) {
                view.setEnabled(editable);
            }
        }

    }

    public static @NonNull String getNewScreen(String screen) {
        switch (screen) {
            case "laboratories":
                screen = "warehouses";
                break;
            case "warehouses":
                screen = "laboratories";
                break;
            case "reagents":
                screen = "instruments";
                break;
            case "instruments":
                screen = "reagents";
                break;
        }
        return screen;
    }


    public static void changeColors(ToggleButton button) {
        if (button.isChecked()) {
            button.setBackgroundColor(ContextCompat.getColor(button.getContext(), R.color.primaryDark));
            button.setTextColor(Color.WHITE);
        } else {
            button.setBackgroundColor(ContextCompat.getColor(button.getContext(), R.color.secondaryDark));
            button.setTextColor(Color.BLACK);
        }
    }

    public static String generateId(String id, String name, String parent) {
        String words[] =  parent.split(" ");
        for (String word : words) {
            id += word.charAt(0);
            if (word.length() > 1)
                id += word.charAt(1);
        }
        id +="-";
        if (id.charAt(0) == 'P') {
            id += name;
        } else {
            words = name.split(" ");
            for (String word : words) {
                id += word.charAt(0);
            }
        }
        return id.toUpperCase();
    }

}
