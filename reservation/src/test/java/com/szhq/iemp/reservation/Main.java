package com.szhq.iemp.reservation;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.ss.formula.functions.T;

import java.util.*;

/**
 * @author wanghao
 * @date 2019/12/2
 */
public class Main {

    public static void main(String[] args) {
        int[] arr = new int[]{3, 2, 4,6,3};
//
//        for(int i=0; i< arr.length -1; i++){
//            for(int j=0; j <arr.length - 1 - i ; j++){
//                if(arr[j] < arr[j + 1]){
//                    int temp = arr[j];
//                    arr[j]= arr[j + 1];
//                    arr[j + 1] = temp;
//                }
//            }
//        }
//        for (int i = 0; i < arr.length; i++)
//            System.out.print(arr[i] + "\t");
        int sum = 0;
        for(int i=0;i<arr.length;i++){
            Map<String, Integer> map = split(arr, i);
            if(map.get("left") != 0 && map.get("right") != 0){
                int k = Math.abs(map.get("left") - map.get("right"));
                System.out.println("ss:" + k);
                sum += k;
            }
        }
        System.out.println(sum);
    }

    public static Map<String, Integer> split(int []arr, int postion){
        int sum = 0;
        int leftMax = 0;
        int rightMax = 0;
        ArrayList<Integer> leftList = new ArrayList<>();
        ArrayList<Integer> rightList = new ArrayList<>();
        for(int i=0; i< postion;i++){
            leftList.add(arr[i]);
        }
        for(int i=postion; i<arr.length;i++){
            rightList.add(arr[i]);
        }
//        System.out.println("left:" + JSONObject.toJSONString(leftList));
//        System.out.println("right:" + JSONObject.toJSONString(rightList));

        for(int i=1;i<leftList.size();i++){
            if(leftMax < arr[i]){
                leftMax=arr[i];
            }
        }
        for(int i=1;i<rightList.size();i++){
            if(rightMax < arr[i]){
                rightMax=arr[i];
            }
        }
        Map<String, Integer> map = new HashMap();
        System.out.println("left:" + leftMax);
        System.out.println("right:" + rightMax);
        map.put("left", leftMax);
        map.put("right", rightMax);
        return map;
    }


}
