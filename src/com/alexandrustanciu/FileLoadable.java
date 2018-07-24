package com.alexandrustanciu;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public interface FileLoadable <T> {
    ArrayList<T> loadFromFile(String path);
    ArrayList<T> loadFromFile(File file) throws IOException;
}
