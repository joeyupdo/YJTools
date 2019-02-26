package com.xixun.joey.aidlset;

interface CardService {
	String	getSoftVersion();
	// 亮度
	boolean	setBrightness(int brightness);
	int		getBrightness();
	// 屏幕开关, true:开屏; false:关屏
	boolean	setScreenOpen(boolean open);
	boolean	isScreenOpen();
	// 屏幕宽高
	int		getScreenWidth();
	int		getScreenHeight();
}