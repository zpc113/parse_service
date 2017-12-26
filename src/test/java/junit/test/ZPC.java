package junit.test;


import java.util.Arrays;

class ZPC {
    public static void main(String[] args) {

        int num = 1211;
        int length = (num + "").length();
        boolean b = false;
        int half = length / 2;
        for (int i = 0 ; i <= half ; i ++) {
            String temp = num + "";
            char begin = temp.charAt(i);
            char end = temp.charAt(length - 1 - i);
            if (begin == end) {
                b = true;
            } else {
                b = false;
                break;
            }
        }
        if (b) {
            System.out.println("修改测试");
        }
    }


}