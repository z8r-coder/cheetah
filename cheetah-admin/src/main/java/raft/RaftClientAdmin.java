package raft;

import java.util.Scanner;

/**
 * @author ruanxin
 * @create 2018-04-20
 * @desc
 */
public class RaftClientAdmin {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print(">");
        String str;
        while (true) {
            String line = scanner.nextLine().trim();
            if (line.length() == 0) {
                System.out.print(">");
                continue;
            }
//            System.out.println(line.trim());
            String[] lineArr = line.split("\\ ");
            if (lineArr[0].equals("get")) {

            } else if (lineArr[0].equals("exit")) {
                break;
            } else {
                System.out.println(line);
                System.out.println("wrong syntax!");
            }
            System.out.print(">");
        }
    }
}
