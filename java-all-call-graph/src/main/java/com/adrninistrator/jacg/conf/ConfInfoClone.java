package com.adrninistrator.jacg.conf;

import java.io.*;

/**
 * @author liyue
 * @date 2022.9.20
 */
public class ConfInfoClone implements Serializable {
    public static ConfInfo myclone(ConfInfo confInfo) {
        ConfInfo anotherCon = null;
        try {
            //在内存中开辟一块缓冲区，将对象序列化成流
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bout);
            out.writeObject(confInfo);

            //找到这一块缓冲区，将字节流反序列化成另一个对象
            ByteArrayInputStream bais = new ByteArrayInputStream(bout.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            anotherCon = (ConfInfo) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return anotherCon;
    }
}
