package com.m4rc310.rcp.ui.utils;

import com.m4rc310.rcp.ui.utils.streaming.MStreamLocal;

public class MAction {
	
	protected final MStreamLocal stream;
	
	public MAction() {
		this.stream = new MStreamLocal();
	}
	
	public MStreamLocal getStream() {
		return stream;
	}
}
