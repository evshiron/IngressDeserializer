package info.evshiron.ingressdeserializer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class IngressDeserializer {

    // Reference to https://gist.github.com/sugyan/8396d473ffae62b664e2, thanks to @sugyan.

    public static String Deserializer(String path) {

        try {

            int count = 0;

            ArrayList<String[]> elements = new ArrayList<String[]>();

            FileInputStream fis = new FileInputStream(path);
            ObjectInputStream ois = new ObjectInputStream(fis);

            while (ois.available() == 0) {

                Object obj = ois.readObject();
                Class cls = obj.getClass();
                String[] arr = new String[Array.getLength(obj)];

                for (int i = 0; i < Array.getLength(obj); i++) {

                    if (cls.getComponentType() == float.class) {
                        arr[i] = String.valueOf(Array.getFloat(obj, i));
                    }
                    if (cls.getComponentType() == short.class) {
                        arr[i] = String.valueOf(Array.getShort(obj, i));
                    }

                }

                // 最初の頂点座標は (vx, vy, vz, tx, ty) になっているようなので分離。
                if (count == 0) {

                    ArrayList<String> vertices = new ArrayList<String>();
                    ArrayList<String> texcoords = new ArrayList<String>();

                    for (int i = 0; i < arr.length; i++) {
                        if (i % 5 < 3) {
                            vertices.add(arr[i]);
                        } else {
                            texcoords.add(arr[i]);
                        }
                    }

                    elements.add(vertices.toArray(new String[0]));
                    elements.add(texcoords.toArray(new String[0]));

                } else {

                    elements.add(arr);

                }

                count++;

            }

            fis.close();
            ois.close();

            StringBuilder sb = new StringBuilder();

            sb.append(String.format("# (x, y, z) count: %d.\n", elements.get(0).length / 3));

            for(int i = 0; i < elements.get(0).length; i += 3) {

                sb.append(String.format("v %s %s %s\n", elements.get(0)[i+0], elements.get(0)[i+1], elements.get(0)[i+2]));

            }

            sb.append(String.format("# (u, v) count: %d.\n", elements.get(1).length / 2));

            for(int i = 0; i < elements.get(1).length; i += 2) {

                sb.append(String.format("vt %s %s\n", elements.get(1)[i+0], elements.get(1)[i+1]));

            }

            sb.append(String.format("# Faces count: %d.\n", elements.get(2).length / 3));

            for(int i = 0; i < elements.get(2).length; i += 3) {

                int v1 = Integer.parseInt(elements.get(2)[i+0]) + 1;
                int v2 = Integer.parseInt(elements.get(2)[i+1]) + 1;
                int v3 = Integer.parseInt(elements.get(2)[i+2]) + 1;

                sb.append(String.format("f %d/%d %d/%d %d/%d\n", v1, v1, v2, v2, v3, v3));

            }

            return sb.toString();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;

    }

    public static void main(String[] args) {

        for(String path : args) {

            System.out.println(Deserializer(path));

        }

    }

}
