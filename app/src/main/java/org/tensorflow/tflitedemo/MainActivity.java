package org.tensorflow.tflitedemo;

import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;


public class MainActivity extends AppCompatActivity {

    private TextInputEditText mInputEditText;
    private MaterialButton mPredictButton;
    private AppCompatTextView mPredictedOutputTextView, mActualOutputTextView;

    private Interpreter mModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wireUpWidgets();
        setUpListeners();
        setUpModel();
    }

    private void wireUpWidgets() {
        mInputEditText = findViewById(R.id.input_number_edit_text);
        mPredictButton = findViewById(R.id.predict_button);
        mPredictedOutputTextView = findViewById(R.id.predicted_result_text_view);
        mActualOutputTextView = findViewById(R.id.actual_result_text_view);
    }

    private void setUpListeners() {
        mPredictButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputString = Objects.requireNonNull(mInputEditText.getText()).toString();
                float prediction = doPrediction(inputString);
                mPredictedOutputTextView.setText("Predicted value is " + prediction);

                float actualOutput = Float.valueOf(inputString) * 2.0f - 1.0f;
                mActualOutputTextView.setText("Actual value is " + actualOutput);
            }
        });
    }

    private float doPrediction(String inputNumber) {
        //Input shape is [1]
        float[] inputValues = new float[1];
        inputValues[0] = Float.valueOf(inputNumber);

        //Output shape is [1][1]
        float[][] outputValues = new float[1][1];

        //Calling the model
        mModel.run(inputValues, outputValues);

        return outputValues[0][0];

    }

    private void setUpModel() {
        try {
            mModel = new Interpreter(loadModel());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private MappedByteBuffer loadModel() throws IOException {
        AssetFileDescriptor fileDescriptor = this.getAssets().openFd("linear_tflite.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }
}
