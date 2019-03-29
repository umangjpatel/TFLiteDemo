import numpy as np
import tensorflow as tf
from tensorflow import keras
from tensorflow.contrib import lite

model = keras.Sequential([keras.layers.Dense(units=1, input_shape=[1])])
model.compile(optimizer='sgd', loss='mean_squared_error')

X = np.array([-1.0, 0.0, 1.0, 2.0, 3.0, 4.0], dtype=float)
Y = np.array([-3.0, -1.0, 1.0, 3.0, 5.0, 7.0], dtype=float)

model.fit(X, Y, epochs=500)
print(model.predict([10.0]))

keras_file = 'linear.h5'
keras.models.save_model(model, keras_file)

converter = lite.TocoConverter.from_keras_model_file(keras_file)
tflite_model = converter.convert()
open("linear_tflite.tflite", "wb").write(tflite_model)

