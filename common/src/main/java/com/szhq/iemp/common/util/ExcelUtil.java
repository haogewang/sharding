package com.szhq.iemp.common.util;

public class ExcelUtil {
	/**
	 *  是否是2003的excel，返回true是2003 
	 * @return
	 */
	public static boolean isExcel2003(String fileName)  {  
		return fileName.matches("^.+\\.(?i)(xls)$");  
	}  

	/**
	 * 是否是2007的excel，返回true是2007 
	 * @return
	 */
	public static boolean isExcel2007(String fileName)  {  
		return fileName.matches("^.+\\.(?i)(xlsx)$");  
	}  

	/**
	 * 验证EXCEL文件
	 * @return
	 */
	public static boolean validateExcel(String fileName){
		if (fileName == null || !(isExcel2003(fileName) || isExcel2007(fileName))){  
			return false;  
		}  
		return true;
	}

}
