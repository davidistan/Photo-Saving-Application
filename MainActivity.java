package com.example.davidtansassignment3;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.io.ByteArrayOutputStream;
import java.security.Provider;
import java.sql.Time;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;


public class MainActivity extends AppCompatActivity {
    Bitmap current = null;
    SQLiteDatabase db = null;
    int count = 0;
    Cursor main_cursor;
    int picture_size = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = openOrCreateDatabase("DB", Context.MODE_PRIVATE, null);
        db_setup();
    }

    void db_setup() {
        db.execSQL("drop table if exists Photos;");
        db.execSQL("create table Photos(PID int, Photo blob, Tag text, Size int);");
    }

    public void capture(View view) {

        Intent x = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(x,1);

    }

    public void save(View view) {
        EditText t = (EditText) findViewById(R.id.tag);
        String t2 = t.getText().toString();
        Context context = getApplicationContext();
        current = current;
        if (current == null) {
            Log.v("PROCESS:", "NO PHOTO");
            Toast no_photo = Toast.makeText(context, "No Photo Has Been Taken", Toast.LENGTH_LONG);
            no_photo.show();
        } else if (t2.length() == 0) {
            Log.v("PROCESS:", "NO TAG");
            Toast no_tag = Toast.makeText(context, "No Tag Has Been Specified. Try Again.", Toast.LENGTH_LONG);
            no_tag.show();

        } else {
            Log.v("PROCESS:", "SAVED");
            Toast saved = Toast.makeText(context, "Photo Saved", Toast.LENGTH_LONG);
            saved.show();
            EditText text = findViewById(R.id.tag);
            String tag = text.getText().toString();
//
            EditText size = (EditText) findViewById(R.id.size);
//
            ByteArrayOutputStream compressed_photo = new ByteArrayOutputStream();
            int num = current.getByteCount();
            setSize(num);
//
            current.compress(Bitmap.CompressFormat.PNG, 0, compressed_photo);
            byte[] photo = compressed_photo.toByteArray();
            ContentValues values = new ContentValues();
            values.put("PID", count);
            values.put("Photo", photo);
            values.put("Tag", tag);
            values.put("Size", num);
//            values.put("Tags", has_substring + "");
            db.insert("Photos", null, values);
            count = count + 1;
//            Log.v("COUNT", count + "");
            Cursor c = db.rawQuery("select * from Photos", null);
            c.moveToFirst();
            Log.v("DATABASE:", "DATABASE CONTENTS");
            Log.v("DATABASE:", "...............................................");
            for (int m = 0; m < c.getCount(); m++) {
                Log.v("DATABASE:", "ID:" + c.getInt(0) + ", PHOTO:" + c.getBlob(1) +  ", TAG:" + c.getString(2) + ", SIZE:" + c.getInt(3));
                c.moveToNext();
            }
        }


    }

    public void setSize(int size) {
        picture_size = size;
    }

    public void load(View view) {
        Log.v("PROCESS:", "LOAD");
        int total = 0;
        boolean number = false;
        int size = picture_size;
//        Log.v("PICTURE_SIZE", picture_size + "");
        db.execSQL("drop table if exists Selected_Photos");
        db.execSQL("create table Selected_Photos(ID int primary key, Picture blob, Tagz text)");
        boolean tags = false;
        ImageView loaded_image = (ImageView) findViewById(R.id.picture);
        TextView photo_id = (TextView) findViewById(R.id.photo_id);
        EditText caption = (EditText) findViewById(R.id.tag);
        String description = caption.getText().toString();
        int a = 0;
        int b = description.length();
        int j = description.length();
        // Use this loop in tag == true and then select the Photos that match each time and then add these
        // photos to the Selected_Photos table
        for (int i = 0; i < description.length(); i++) {
            if (description.charAt(i) == ';') {
                tags = true;
            }

        }
//
//        }
        String d1 = "%;" + description;
        String c2 = description + "%";
        String c3 = "%;" + description + "%";
        String d2 = "%; " + description;
        String c5 = description + "%";
        String d3 = description + ";%";
        String d4 = "%;" + description + ";%";
        String d5 = "%; " + description + ";%";
//        Log.v("CONTAINS0", tags + "");
        Log.v("PROCESS:", "SUBSTRING:" + d1);
        Log.v("PROCESS:", "SUBSTRING:" + d2);
        Log.v("PROCESS:", "SUBSTRING:" + d3);
        Log.v("PROCESS:", "SUBSTRING:" + d4);
        Log.v("PROCESS:", "SUBSTRING:" + d5);
        Log.v("PROCESS:", "SUBSTRING:" + c2);
        Log.v("PROCESS:", "SUBSTRING:" + c3);
        Log.v("PROCESS:", "SUBSTRING:" + c5);


        EditText bytes = (EditText) findViewById(R.id.size);
        String bites = bytes.getText().toString();
        if (bites.length() > 0) {
            Log.v("PROCESS:", "SIZE IS NOT EMPTY");
            int bite_num = (int) bites.charAt(0);
            int power = 0;
            for (int counter = bites.length() - 1; counter >= 0; counter--) {
                //            Log.v("COUNTER:",counter + "");
                double factor = Math.pow(10, power);
                //            Log.v("POWERRRR:", factor + "");
                int digit = (int) bites.charAt(counter) - 48;
                //            Log.v("DIGGITTTT", digit + "");
                total = total + ((int) factor * digit);
                power = power + 1;
            }
        } else {
            Log.v("PROCESS:", "SIZE IS EMPTY");
        }


        if (bites.length() > 0 && description.length() > 0) {
            Log.v("PROCESS:", "TAG AND SIZE SPECIFIED");
//            Log.v("TOTAL", "In BOTH");
            boolean marked = false;
            if (tags == true) {
                Log.v("PROCESS:", "MULTIPLE TAGS SPECIFIED");
//                Log.v("STRINGZ", "IN TAGS");
                for (int i = 0; i < description.length(); i++) {
//                    Log.v("MARKED", marked + ":" + i);
                    if (description.charAt(i) == ';') {
                        b = i;
                        String sub = "%" + description.substring(a, b) + "%";
//                        String sub2 = "%" + " " + description.substring(a,b) + "%";
                        Log.v("PROCESS:", "" + sub + "");
                        if (sub.charAt(1) == ' ') {
                            sub = "%" + sub.substring(2);
                            Log.v("PROCESS:", "APPENDED SUBSTRING:" + sub);
                        }
                        a = i + 1;
                        j = i;
                        if (marked == false) {
//                            Log.v("MARKED", "IN FIRST STATEMENT");

                            String find = "select P.PID, P.Photo, P.Tag from Photos P where P.Tag like '" + sub + "' and Size <> 0 and  Size > '" + (total * 0.75) + "' and Size < '" + (total * 1.25) + "' order by P.PID asc;";
                            Cursor c = db.rawQuery(find, null);
                            c.moveToFirst();
//
                            for (int x = 0; x < c.getCount(); x++) {
//                                Log.v("STRINGZ", x + "- IN FOR LOOP");
                                int ID = c.getInt(0);
                                byte[] s_photo = c.getBlob(1);
                                String t = c.getString(2);
                                ContentValues s_values = new ContentValues();
                                s_values.put("ID", ID);
                                s_values.put("Picture", s_photo);
                                s_values.put("Tagz", t);
                                db.insert("Selected_Photos", null, s_values);
                                Bitmap b_map = BitmapFactory.decodeByteArray(s_photo, 0, s_photo.length);
                                c.moveToNext();

                            }
                            if (c.getCount() > 0) {
                                marked = true;
                            }
//                            Log.v("STRINGZ", sub);
                        } else if (marked == true) {
//                            Log.v("MARKED", "IN ELSE STATEMENT");
                            String all = "select * from Selected_Photos order by ID asc";
                            Cursor curse = db.rawQuery(all, null);
//                            Log.v("MARKED", "Selected_Photos Size:" + curse.getCount() + "");
                            String find = "select P.PID, P.Photo, P.Tag from Photos P, Selected_Photos S where P.Tag like '" + sub + "' and Size > '" + (total * 0.75) + "' and Size < '" + (total * 1.25) + "' order by P.PID asc;";
                            Cursor c = db.rawQuery(find, null);
                            c.moveToFirst();
//
                            for (int x = 0; x < c.getCount(); x++) {
//                                Log.v("STRINGZ", x + "- IN FOR LOOP");
                                int ID = c.getInt(0);
//                                Log.v("MARKED", "ID:" + ID);
                                byte[] s_photo = c.getBlob(1);
                                String t = c.getString(2);
                                ContentValues s_values = new ContentValues();
                                s_values.put("ID", ID);
                                s_values.put("Picture", s_photo);
                                s_values.put("Tagz", t);
                                db.insert("Selected_Photos", null, s_values);
                                Bitmap b_map = BitmapFactory.decodeByteArray(s_photo, 0, s_photo.length);
                                c.moveToNext();
                                String string = "select * from Selected_Photos order by ID asc";
                                Cursor curses = db.rawQuery(string, null);
//                                Log.v("MARKED", "SELECTED PHOTOS COUNT" + curses.getCount());


                            }

                        }
                    } else if (i == description.length() - 1) {
                        if (marked == false) {
                            String subz = "%" + description.substring(a, description.length()) + "%";
//                            String subz2 = "%" + " " + description.substring(a, description.length()) + "%";
                            Log.v("PROCESS:", "" + subz + "");
                            if (subz.charAt(1) == ' ') {
                                subz = "%" + subz.substring(2);
                                Log.v("PROCESS:", "APPENDED SUBSTRING:" + subz);
                            }
                            String find = "select P.PID, P.Photo, P.Tag from Photos P where P.Tag like '" + subz + "' and Size > '" + (total * 0.75) + "' and Size < '" + (total * 1.25) + "' order by P.PID asc;";
                            Cursor c = db.rawQuery(find, null);
                            c.moveToFirst();
                            for (int x = 0; x < c.getCount(); x++) {
//                                Log.v("STRINGZ", x + "Outer Loop");
                                int ID = c.getInt(0);
                                byte[] s_photo = c.getBlob(1);
                                String t = c.getString(2);
                                ContentValues s_values = new ContentValues();
                                s_values.put("ID", ID);
                                s_values.put("Picture", s_photo);
                                s_values.put("Tagz", t);
                                db.insert("Selected_Photos", null, s_values);
                                Bitmap b_map = BitmapFactory.decodeByteArray(s_photo, 0, s_photo.length);
                                c.moveToNext();
                            }

//                            Log.v("STRINGZ", subz);
                        } else if (marked == true) {
//                            Log.v("MARKED", "MARKED IS TRUE");
                            String subz = "%" + description.substring(a, description.length()) + "%";
//                            String subz2 = "%" + " " + description.substring(a, description.length()) + "%";
                            Log.v("PROCESS:", "" + subz + "");
                            if (subz.charAt(1) == ' ') {
                                subz = "%" + subz.substring(2);
                                Log.v("PROCESS:", "APPENDED SUBSTRING:" + subz);
                            }
                            String find = "select P.PID, P.Photo, P.Tag from Photos P, Selected_Photos S where P.Tag like '" + subz + "' and Size > '" + (total * 0.75) + "' and Size < '" + (total * 1.25) + "' order by P.PID asc;";
                            Cursor c = db.rawQuery(find, null);
                            c.moveToFirst();
//                            Log.v("MARKED", "count:" + c.getCount() + "");
                            for (int x = 0; x < c.getCount(); x++) {
//                                Log.v("STRINGZ", x + "Outer Loop");
                                int ID = c.getInt(0);
                                byte[] s_photo = c.getBlob(1);
                                String t = c.getString(2);
                                ContentValues s_values = new ContentValues();
                                s_values.put("ID", ID);
                                s_values.put("Picture", s_photo);
                                s_values.put("Tagz", t);
                                db.insert("Selected_Photos", null, s_values);
                                Bitmap b_map = BitmapFactory.decodeByteArray(s_photo, 0, s_photo.length);
                                c.moveToNext();
                            }

//                            Log.v("STRINGZ", subz);

                        }
                    }
                }

                String fotos = "select Picture, Tagz, ID from Selected_Photos order by ID asc";
                Cursor list = db.rawQuery(fotos, null);
                main_cursor = list;
                main_cursor.moveToFirst();
                if (list.getCount() > 0) {
//
                    int id = list.getInt(2);
                    photo_id.setText("" + id + "");
                    byte[] photo = list.getBlob(0);
                    Bitmap picture = BitmapFactory.decodeByteArray(photo, 0, photo.length);
                    loaded_image.setBackgroundResource(0);
                    loaded_image.setImageBitmap(picture);
                } else if (list.getCount() == 0) {
                    photo_id.setText("");
                    loaded_image.setImageBitmap(null);
                    loaded_image.setBackgroundResource(0);
                    loaded_image.setBackgroundResource(R.drawable.empty);

                }


//
            } else if (tags == false) {
                Log.v("PROCESS:", "SINGLE TAG SPECIFIED");
                String search = "select Photo, Tag, PID from Photos where Tag like '" + d1 + "' or Tag like '" + d2 + "' or Tag like '" + d3 + "' or Tag like '" + d4 + "' or Tag like '" + d5 + "' or Tag = '" + description + "' and Size > '" + (total * 0.75) + "' and Size < '" + (total * 1.25) + "';";
                Cursor c = db.rawQuery(search, null);
                main_cursor = c;
                c.moveToFirst();
                main_cursor.moveToFirst();
                if (c.getCount() > 0) {

                    int id = c.getInt(2);
                    photo_id.setText("" + id + "");
                    byte[] photo = c.getBlob(0);
                    Bitmap picture = BitmapFactory.decodeByteArray(photo, 0, photo.length);
                    loaded_image.setBackgroundResource(0);
                    loaded_image.setImageBitmap(picture);
                } else if (c.getCount() == 0) {
                    photo_id.setText("");
                    loaded_image.setImageBitmap(null);
                    loaded_image.setBackgroundResource(0);
                    loaded_image.setBackgroundResource(R.drawable.empty);

                }

            }
        } else if (description.length() > 0 && bites.length() == 0) {
            Log.v("PROCESS:", "ONLY TAG SPECIFIED");
            boolean marked = false;
            if (tags == true) {
                Log.v("PROCESS:", "MULTIPLE TAGS SPECIFIED");
//                Log.v("STRINGZ", "IN TAGS");
                for (int i = 0; i < description.length(); i++) {
//                    Log.v("MARKED", marked + ":" + i);
                    if (description.charAt(i) == ';') {
                        b = i;
                        String sub = "%" + description.substring(a, b) + "%";
//                        String sub2 = "%" + description.substring(a, b) + "%";
                        Log.v("PROCESS:", "" + sub + "");
                        if (sub.charAt(1) == ' ') {
                            sub = "%" + sub.substring(2);
                            Log.v("PROCESS:", "APPENDED STRING:" + sub + "");
                        }
//                        Log.v("PROCESS:", "SECOND STRING:" + sub2 + "");
//                        Log.v("PROCESS:", "CHARACTERS:" + description.charAt(i + 1));
                        a = i + 1;
                        j = i;

                        if (marked == false) {
//                            Log.v("MARKED", "IN FIRST STATEMENT");

                            String find = "select P.PID, P.Photo, P.Tag from Photos P where P.Tag like '" + sub + "' order by P.PID asc;";
                            Cursor c = db.rawQuery(find, null);
                            c.moveToFirst();
//
                            for (int x = 0; x < c.getCount(); x++) {
//                                Log.v("STRINGZ", x + "- IN FOR LOOP");
                                int ID = c.getInt(0);
                                byte[] s_photo = c.getBlob(1);
                                String t = c.getString(2);
                                ContentValues s_values = new ContentValues();
                                s_values.put("ID", ID);
                                s_values.put("Picture", s_photo);
                                s_values.put("Tagz", t);
                                db.insert("Selected_Photos", null, s_values);
                                Bitmap b_map = BitmapFactory.decodeByteArray(s_photo, 0, s_photo.length);
                                c.moveToNext();

                            }
                            if (c.getCount() > 0) {
                                marked = true;
                            }
//                            Log.v("STRINGZ", sub);
                        } else if (marked == true) {
//                            Log.v("MARKED", "IN ELSE STATEMENT");
                            String all = "select * from Selected_Photos";
                            Cursor curse = db.rawQuery(all, null);
//                            Log.v("MARKED", "Selected_Photos Size:" + curse.getCount() + "");
                            String find = "select P.PID, P.Photo, P.Tag from Photos P, Selected_Photos S where P.Tag like '" + sub + "' order by P.PID asc;";
                            Cursor c = db.rawQuery(find, null);
                            c.moveToFirst();
//
                            for (int x = 0; x < c.getCount(); x++) {
//                                Log.v("STRINGZ", x + "- IN FOR LOOP");
                                int ID = c.getInt(0);
//                                Log.v("MARKED", "ID:" + ID);
                                byte[] s_photo = c.getBlob(1);
                                String t = c.getString(2);
                                ContentValues s_values = new ContentValues();
                                s_values.put("ID", ID);
                                s_values.put("Picture", s_photo);
                                s_values.put("Tagz", t);
                                db.insert("Selected_Photos", null, s_values);
                                Bitmap b_map = BitmapFactory.decodeByteArray(s_photo, 0, s_photo.length);
                                c.moveToNext();
                                String string = "select * from Selected_Photos order by ID asc";
                                Cursor curses = db.rawQuery(string, null);
//                                Log.v("MARKED", "SELECTED PHOTOS COUNT" + curses.getCount());


                            }

                        }
                    } else if (i == description.length() - 1) {
                        if (marked == false) {
                            String subz = "%" + description.substring(a, description.length()) + "%";
//                            String subz2 = "%" + " " + description.substring(a, description.length()) + "%";
                            Log.v("PROCESS:", "" + subz + "");
                            if (subz.charAt(1) == ' ') {
                                subz = "%" + subz.substring(2);
                                Log.v("PROCESS:", "APPENDED SUBSTRING:" + subz);
                            }
                            String find = "select P.PID, P.Photo, P.Tag from Photos P where P.Tag like '" + subz + "' order by P.PID asc;";
                            Cursor c = db.rawQuery(find, null);
                            c.moveToFirst();
                            for (int x = 0; x < c.getCount(); x++) {
//                                Log.v("STRINGZ", x + "Outer Loop");
                                int ID = c.getInt(0);
                                byte[] s_photo = c.getBlob(1);
                                String t = c.getString(2);
                                ContentValues s_values = new ContentValues();
                                s_values.put("ID", ID);
                                s_values.put("Picture", s_photo);
                                s_values.put("Tagz", t);
                                db.insert("Selected_Photos", null, s_values);
                                Bitmap b_map = BitmapFactory.decodeByteArray(s_photo, 0, s_photo.length);
                                c.moveToNext();
                            }

//                            Log.v("STRINGZ", subz);
                        } else if (marked == true) {
//                            Log.v("MARKED", "MARKED IS TRUE");
                            String subz = "%" + description.substring(a, description.length()) + "%";
                            Log.v("PROCESS:", subz);
//                            String subz2 = "%" + " " + description.substring(a, description.length()) + "%";
                            if (subz.charAt(1) == ' ') {
                                subz = "%" + subz.substring(2);
                                Log.v("PROCESS:", "APPENDED SUBSTRING:" + subz + "");
                            }

                            String find = "select P.PID, P.Photo, P.Tag from Photos P, Selected_Photos S where P.Tag like '" + subz + "' order by P.PID asc;";
                            Cursor c = db.rawQuery(find, null);
                            c.moveToFirst();
//                            Log.v("MARKED", "count:" + c.getCount() + "");
                            for (int x = 0; x < c.getCount(); x++) {
//                                Log.v("STRINGZ", x + "Outer Loop");
                                int ID = c.getInt(0);
                                byte[] s_photo = c.getBlob(1);
                                String t = c.getString(2);
                                ContentValues s_values = new ContentValues();
                                s_values.put("ID", ID);
                                s_values.put("Picture", s_photo);
                                s_values.put("Tagz", t);
                                db.insert("Selected_Photos", null, s_values);
                                Bitmap b_map = BitmapFactory.decodeByteArray(s_photo, 0, s_photo.length);
                                c.moveToNext();
                            }

//                            Log.v("STRINGZ", subz);

                        }
                    }
                }

                String fotos = "select Picture, Tagz, ID from Selected_Photos order by ID asc";
                Cursor list = db.rawQuery(fotos, null);
                main_cursor = list;
                main_cursor.moveToFirst();
                if (list.getCount() > 0) {
                    //        Log.v("FIRST STRING", caption.getText().appe);
//                    //        Log.v("SECOND STRING", second);
//                    Log.v("POSITION", list.getPosition() + "");
//                    Log.v("MYTAG", list.getString(1));
//                    Log.v("MAIN_CURSOR", main_cursor.getString(1));
                    int id = list.getInt(2);
                    photo_id.setText("" + id + "");
                    byte[] photo = list.getBlob(0);
                    Bitmap picture = BitmapFactory.decodeByteArray(photo, 0, photo.length);
                    loaded_image.setBackgroundResource(0);
                    loaded_image.setImageBitmap(picture);
                } else if (list.getCount() == 0) {
                    photo_id.setText("");
                    loaded_image.setImageBitmap(null);
                    loaded_image.setBackgroundResource(0);
                    loaded_image.setBackgroundResource(R.drawable.empty);

                }



            } else if (tags == false) {
                Log.v("PROCESS:", "SINGLE TAG SPECIFIED");
                String search = "select Photo, Tag, PID from Photos where Tag like '" + d1 + "' or Tag like '" + d2 + "' or Tag like '" + d3 + "' or Tag like '" + d4 + "' or Tag like '" + d5 + "' or Tag = '" + description + "';";
                Cursor c = db.rawQuery(search, null);
                main_cursor = c;
                c.moveToFirst();
                main_cursor.moveToFirst();
                if (c.getCount() > 0) {
                    //        Log.v("FIRST STRING", caption.getText().appe);
                    //        Log.v("SECOND STRING", second);
                    //            Log.v("POSITION", c.getPosition() + "");
                    //            Log.v("MYTAG", c.getString(1));
                    //            Log.v("MAIN_CURSOR", main_cursor.getString(1));
                    int id = c.getInt(2);
                    photo_id.setText("" + id + "");
                    byte[] photo = c.getBlob(0);
                    Bitmap picture = BitmapFactory.decodeByteArray(photo, 0, photo.length);
                    loaded_image.setBackgroundResource(0);
                    loaded_image.setImageBitmap(picture);
                } else if (c.getCount() == 0) {
                    photo_id.setText("");
                    loaded_image.setImageBitmap(null);
                    loaded_image.setBackgroundResource(0);
                    loaded_image.setBackgroundResource(R.drawable.empty);

                }

            }

        } else if (bites.length() > 0 && description.length() == 0) {
            Log.v("PROCESS:", "ONLY SIZE SPECIFIED");
//            Log.v("STILL THE TOTAL:", total + "");
//            Log.v("IN BITES", "SIZE ONLY");


//
            String search = "select Photo, Tag, PID from Photos P where P.Size > '" + (total * 0.75) + "' and P.Size < '" + (total * 1.25) + "' order by P.PID asc;";
            Cursor c = db.rawQuery(search, null);
            main_cursor = c;
            c.moveToFirst();
            main_cursor.moveToFirst();
//            Log.v("IN BITES", "THE SIZE:" + c.getInt(2));
//            Log.v("IN BITES", "count:" + c.getCount() + "");
            if (c.getCount() > 0) {
                //        Log.v("FIRST STRING", caption.getText().appe);
                //        Log.v("SECOND STRING", second);
                Log.v("POSITION", c.getPosition() + "");
                Log.v("MYTAG", c.getString(1));
                int id = c.getInt(2);
                photo_id.setText("" + id + "");
                Log.v("MAIN_CURSOR", main_cursor.getString(1));
                byte[] photo = c.getBlob(0);
                Bitmap picture = BitmapFactory.decodeByteArray(photo, 0, photo.length);
                loaded_image.setBackgroundResource(0);
                loaded_image.setImageBitmap(picture);
            } else if (c.getCount() == 0) {
                photo_id.setText("");
                loaded_image.setImageBitmap(null);
                loaded_image.setBackgroundResource(0);
                loaded_image.setBackgroundResource(R.drawable.empty);
            }


//        } else {
//            loaded_image.setImageBitmap(null);
//            loaded_image.setBackgroundResource(0);
//            loaded_image.setBackgroundResource(R.drawable.no_result);
//        }


        }
    }

    public void forward(View view) {
        Log.v("PROCESS:", "NEXT PHOTO");
        if (main_cursor.getPosition() < main_cursor.getCount() - 1) {
            ImageView image = (ImageView) findViewById(R.id.picture);
            TextView photo_id = (TextView) findViewById(R.id.photo_id);
            main_cursor.moveToNext();
            int id = main_cursor.getInt(2);
            photo_id.setText("" + id + "");
            byte[] next = main_cursor.getBlob(0);
            Bitmap next_picture = BitmapFactory.decodeByteArray(next, 0, next.length);
            image.setBackgroundResource(0);
            image.setImageBitmap(next_picture);
        }

    }

    public void back(View view) {
        Log.v("PROCESS:", "PREVIOUS PHOTO");
        if (main_cursor.getPosition() > 0) {
            ImageView image = (ImageView) findViewById(R.id.picture);
            TextView photo_id = (TextView) findViewById(R.id.photo_id);
            main_cursor.moveToPrevious();
            int id = main_cursor.getInt(2);
            photo_id.setText("" + id + "");
            byte[] previous = main_cursor.getBlob(0);
            Bitmap next_picture = BitmapFactory.decodeByteArray(previous, 0, previous.length);
            image.setBackgroundResource(0);
            image.setImageBitmap(next_picture);
        }

    }

    protected void onActivityResult(int rc, int resc, Intent data) {
        TextView id = (TextView)  findViewById(R.id.photo_id);
        id.setText("" + count + "");
        ImageView image = null;
        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
        image = (ImageView) findViewById(R.id.picture);
        image.setBackgroundResource(0);
        image.setImageBitmap(bitmap);
        current = bitmap;
        int count = bitmap.getByteCount();
        Log.v("I AM COUNTING:", count + "");
        EditText value = (EditText) findViewById(R.id.size);
        value.setText("" + count + "");


    }

    public void tag(View view) {
        int key =
        Log.v("MYTAG", "button clicked");
        if (view.requestFocus()) {
            InputMethodManager input = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            input.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
            EditText text = (EditText) findViewById(R.id.tag);





        }



    }




}
