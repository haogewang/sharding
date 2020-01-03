package com.szhq.iemp.reservation;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public class Test1 {

    static String[] nameArr;
    static int[][] matrix;
    // 所有名称的结果集
    private List<List<String>> result = new ArrayList<>();
    // 当前结点是否还有下一个结点
    private boolean noNext = false;

    public static void main(String[] args) {
        Cell c1 = new Cell();
        List n = new ArrayList<>();
        n.add("B");
        n.add("C");
        c1.setName("A");
        c1.setNeibers(n);
        Cell c2 = new Cell();
        List n2 = new ArrayList<>();
        c2.setName("B");
        n2.add("D");
        c2.setNeibers(n2);
        Cell c3 = new Cell();
        List n3 = new ArrayList<>();
        c3.setName("C");
        n3.add("D");
        n3.add("B");
        c3.setNeibers(n3);
        Cell c4 = new Cell();
        List n4 = new ArrayList<>();
        c4.setName("D");
        c4.setNeibers(n4);
        List cells = new ArrayList<>();
        cells.add(c1);
        cells.add(c2);
        cells.add(c3);
        cells.add(c4);
        nameArr = transferToNameArr(cells);
        matrix = getMatrix(cells);
        Test1 test = new Test1();
        List<List<String>> result = test.getAllName();
//        System.out.println(JSONObject.toJSONString(result));
        Collections.reverse(result.get(0));
        System.out.println(JSONObject.toJSONString(result.get(0)));
    }

    public List<List<String>> getAllName() {
        for (int i = 0; i < countPathNumber(); i++) {
            //用于存储遍历过的点
            List<String> path = new LinkedList<>();
            noNext = false;
            DFS(0, path);
            if(result.isEmpty()){
                result.add(path);
            }
            else{
                if(result.get(0).size() < path.size()){
                    result.clear();
                    result.add(path);
                }
            }
        }
        return result;
    }

    /**
     * 递归遍历
     */
    private void DFS(int begin, List<String> path) {
        // 将当前结点加入记录，横坐标节点
        path.add(nameArr[begin]);
        // 标记回滚位置
        int rollBackNum = -1;
        for (int i = 0; i < matrix.length; i++) {
            if ((matrix[begin][i] > 0)) {
                // 暂时加入邻结点（纵坐标节点），判断新路径是否已遍历过
                path.add(nameArr[i]);
                if (containBranch(result, path)) {
                    // 路径已存在，将相邻结点再移出记录
                    path.remove(nameArr[i]);
                    rollBackNum = i;
                    continue;
                } else {
                    // 路径为新路径，准备进入递归，将相邻结点移出，防止重复添加
                    path.remove(nameArr[i]);
                    DFS(i, path);
                }
            }
            // 终止递归
            if (noNext) {
                return;
            }
        }
        if (rollBackNum > -1) {
            // 循环结束仅有一个相邻结点，从这个相邻结点往下递归
            DFS(rollBackNum, path);
        } else {
            // 当前结点没有相邻结点，设置flag结束递归
            noNext = true;
        }
    }

    /**
     * 计算路径的分支数量
     */
    private int countPathNumber() {
        int[] numberArray = new int[matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                if (matrix[j][i] > 0) {
                    numberArray[j]++;
                }
            }
        }
        int number = 1;
        for (int k = 0; k < matrix.length; k++) {
            if (numberArray[k] > 1) {
                number++;
            }
        }
        return number;
    }

    /**
     * 判断当前路径是否被已有路径的结果集合所包含
     */
    private boolean containBranch(List<List<String>> result, List<String> edges) {
        for(int i = 0; i < result.size(); i++) {
            List<String> list = result.get(i);
            if (list.containsAll(edges)) {
                return true;
            }
        }
        return false;
    }

    /**
     * list转成保存名称的数组
     */
    private static String[] transferToNameArr(List<Cell> cells) {
        String[] result =  new String[cells.size()];
        for (int i = 0; i <cells.size() ; i++) {
            result[i] = cells.get(i).getName();
        }
        return result;
    }

    /**
     * 转为矩阵
     */
    private static int[][] getMatrix(List<Cell> cells) {
        int[][] matrix =  new int[cells.size()][cells.size()];
        String[] names =  transferToNameArr(cells);

// System.out.println(JSONObject.toJSONString(vertex));
        for(int i = 0; i <cells.size() ; i++) {
            Cell cell = cells.get(i);
            int ii = getIndex(cell.getName(), names);
            List<String> neibers = cell.getNeibers();
            for (int j = 0; j < neibers.size(); j++) {
                int jj = getIndex(neibers.get(j), names);
// System.out.println(neibers.get(j)+"," + JSONObject.toJSONString(vertex));
// System.out.println(“ii:”+ii+",jj"+jj);
                matrix[ii][jj] =1;
            }
        }
// System.out.println(JSONObject.toJSONString(matrix));
        return matrix;
    }

    /**
     * 根据名称获取下标
     */
    private static int getIndex(String name, String[] nameArr) {
        for (int i = 0; i <nameArr.length ; i++) {
            if(nameArr[i].equals(name)){
                return i;
            }
        }
        return -1;
    }
}
