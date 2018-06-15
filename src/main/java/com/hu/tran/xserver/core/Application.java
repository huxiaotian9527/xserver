package com.hu.tran.xserver.core;

import com.hu.tran.xserver.pack.PackMapper;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author hutiantian
 * @create 2018/6/15 11:35
 * @since 1.0.0
 */
public class Application {
    private static final Logger log = Logger.getLogger(Application.class);

    private static final int port = 8888;

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(port);
        String packPath = Application.class.getResource("/").getPath()+"pack";
        if(PackMapper.init(packPath)==null){
            log.error("初始化失败！");
            System.exit(0);
        }
        while (true) {
            Socket socket = server.accept();
            Task task = new Task(socket);
            TaskThreadPool pool = new TaskThreadPool();
            pool.execute(task);
        }
    }
}
