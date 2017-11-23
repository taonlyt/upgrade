/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package upgrade;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;

/**
 *
 * @author luotao
 */
public class FileTransferClient extends Socket {

    private static final String SERVER_IP = "127.0.0.1"; // 服务端IP  
    private static final int SERVER_PORT = 8899; // 服务端端口  

    private Socket client;

    private FileInputStream fis;

    private DataOutputStream dos;

    /**
     * 入口
     *
     * @param args
     */
    public static void main(String[] args) {
        try {
            FileTransferClient client = new FileTransferClient(); // 启动客户端连接  
            client.sendFile(); // 传输文件  
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 构造函数与服务器建立连接
     *
     * @throws Exception
     */
    public FileTransferClient() throws Exception {
        super(SERVER_IP, SERVER_PORT);
        this.client = this;
        client.setSoTimeout(5000);
        System.out.println("connected "+client.getInetAddress().getHostAddress()+" "+client.getPort());
    }

    /**
     * 向服务端传输文件
     *
     * @throws Exception
     */
    public void sendFile() throws Exception {
        try {
            File file = new File("/home/luotao/hs_err_pid2705.log");
            if (file.exists()) {
                fis = new FileInputStream(file);
                dos = new DataOutputStream(client.getOutputStream());

                // 文件名和长度  
                dos.writeUTF(file.getName());
                dos.flush();
                dos.writeLong(file.length());
                dos.flush();

                // 开始传输文件  
                System.out.println("======== 开始传输文件 ========");
                byte[] bytes = new byte[1024];
                int length = 0;
                long progress = 0;
                while ((length = fis.read(bytes, 0, bytes.length)) != -1) {
                    dos.write(bytes, 0, length);
                    dos.flush();
                    progress += length;
                    System.out.print("| " + (100 * progress / file.length()) + "% |");
                }
                System.out.println();
                System.out.println("======== 文件传输成功 ========");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                fis.close();
            }
            if (dos != null) {
                dos.close();
            }
            client.close();
        }
    }

}
