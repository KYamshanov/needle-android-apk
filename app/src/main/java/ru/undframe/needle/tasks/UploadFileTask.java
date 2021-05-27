package ru.undframe.needle.tasks;

import android.os.AsyncTask;
import android.os.Build;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.function.Consumer;

import ru.undframe.needle.utils.GlobalProperties;
import ru.undframe.needle.utils.MultipartUtility;

public class UploadFileTask extends AsyncTask<Void, Void, Integer> {

    private File file;
    private Consumer<Integer> action;
    private static File emptyFile;
    static{
        try {
            emptyFile = File.createTempFile("temptile", ".tmp");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public UploadFileTask( File file, Consumer<Integer> action) {
        this.file = file;
        this.action = action;
    }

    @Override
    protected Integer doInBackground(Void... params) {
        try {



                String requestUrl = "http://" + GlobalProperties.SERVER_ADDRESS + "/uploadfile";
                MultipartUtility multipart = new MultipartUtility(requestUrl, Charset.defaultCharset().name());

                if(file!=null)
                    multipart.addFilePart("file", file);


                List<String> response = multipart.finish();

                StringBuilder stringJson = new StringBuilder();

                for (String s : response) {
                    stringJson.append(s);
                }

                JSONObject jsonObject = new JSONObject(stringJson.toString());
                return jsonObject.getInt("status");
        } catch (Exception e) {
            e.printStackTrace();

        }
        return 1;
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            action.accept(result);
        }
    }

}
