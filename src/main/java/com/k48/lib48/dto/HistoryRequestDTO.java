package com.k48.lib48.dto;

import com.k48.lib48.myEnum.EtatOpperation;
import com.k48.lib48.myEnum.TypeOpperation;

public record HistoryRequestDTO(
	String userName,
	String bookTitle,
	TypeOpperation typeOpperation,
	EtatOpperation etatOpperation,
	String details) { }
