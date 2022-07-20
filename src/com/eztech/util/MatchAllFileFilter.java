/**
 * 
 */
package com.eztech.util;

import java.io.File;
import java.io.FileFilter;

public class MatchAllFileFilter implements FileFilter {
	public boolean accept(File pathname) {
		return true;
	}
}