package br.com.nsym.domain.model.entity.fiscal.tools;


import java.math.BigDecimal;

import javax.enterprise.context.RequestScoped;

import br.com.nsym.domain.model.entity.tools.Uf;
@RequestScoped
public class OrigemXDestino {
	
	private BigDecimal[][] tabela = new BigDecimal[28][28];
	
	
	
	public OrigemXDestino() {
		// AC - ACRE - 0
		tabela[0][0]=new BigDecimal("17"); tabela[0][7]=new BigDecimal("12"); tabela[0][14]=new BigDecimal("12"); tabela[0][21]=new BigDecimal("12");
		tabela[0][1]=new BigDecimal("12"); tabela[0][8]=new BigDecimal("12"); tabela[0][15]=new BigDecimal("12"); tabela[0][22]=new BigDecimal("12");
		tabela[0][2]=new BigDecimal("12"); tabela[0][9]=new BigDecimal("12"); tabela[0][16]=new BigDecimal("12"); tabela[0][23]=new BigDecimal("12");
		tabela[0][3]=new BigDecimal("12"); tabela[0][10]=new BigDecimal("12"); tabela[0][17]=new BigDecimal("12"); tabela[0][24]=new BigDecimal("12");
		tabela[0][4]=new BigDecimal("12"); tabela[0][11]=new BigDecimal("12"); tabela[0][18]=new BigDecimal("12"); tabela[0][25]=new BigDecimal("12");
		tabela[0][5]=new BigDecimal("12"); tabela[0][12]=new BigDecimal("12"); tabela[0][19]=new BigDecimal("12"); tabela[0][26]=new BigDecimal("12");
		tabela[0][6]=new BigDecimal("12"); tabela[0][12]=new BigDecimal("12"); tabela[0][20]=new BigDecimal("12"); tabela[0][27]=new BigDecimal("4");
		// AL - ALAGOAS - 1 
		tabela[1][0]=new BigDecimal("12"); tabela[1][7]=new BigDecimal("12"); tabela[1][14]=new BigDecimal("12"); tabela[1][21]=new BigDecimal("12");
		tabela[1][1]=new BigDecimal("17"); tabela[1][8]=new BigDecimal("12"); tabela[1][15]=new BigDecimal("12"); tabela[1][22]=new BigDecimal("12");
		tabela[1][2]=new BigDecimal("12"); tabela[1][9]=new BigDecimal("12"); tabela[1][16]=new BigDecimal("12"); tabela[1][23]=new BigDecimal("12");
		tabela[1][3]=new BigDecimal("12"); tabela[1][10]=new BigDecimal("12"); tabela[1][17]=new BigDecimal("12"); tabela[1][24]=new BigDecimal("12");
		tabela[1][4]=new BigDecimal("12"); tabela[1][11]=new BigDecimal("12"); tabela[1][18]=new BigDecimal("12"); tabela[1][25]=new BigDecimal("12");
		tabela[1][5]=new BigDecimal("12"); tabela[1][12]=new BigDecimal("12"); tabela[1][19]=new BigDecimal("12"); tabela[1][26]=new BigDecimal("12");
		tabela[1][6]=new BigDecimal("12"); tabela[1][12]=new BigDecimal("12"); tabela[1][20]=new BigDecimal("12"); tabela[1][27]=new BigDecimal("4");
		// AM - AMAZONAS - 2
		tabela[2][0]=new BigDecimal("12"); tabela[2][7]=new BigDecimal("12"); tabela[2][14]=new BigDecimal("12"); tabela[2][21]=new BigDecimal("12");
		tabela[2][1]=new BigDecimal("12"); tabela[2][8]=new BigDecimal("12"); tabela[2][15]=new BigDecimal("12"); tabela[2][22]=new BigDecimal("12");
		tabela[2][2]=new BigDecimal("18"); tabela[2][9]=new BigDecimal("12"); tabela[2][16]=new BigDecimal("12"); tabela[2][23]=new BigDecimal("12");
		tabela[2][3]=new BigDecimal("18"); tabela[2][10]=new BigDecimal("12"); tabela[2][17]=new BigDecimal("12"); tabela[2][24]=new BigDecimal("12");
		tabela[2][4]=new BigDecimal("12"); tabela[2][11]=new BigDecimal("12"); tabela[2][18]=new BigDecimal("12"); tabela[2][25]=new BigDecimal("12");
		tabela[2][5]=new BigDecimal("12"); tabela[2][12]=new BigDecimal("12"); tabela[2][19]=new BigDecimal("12"); tabela[2][26]=new BigDecimal("12");
		tabela[2][6]=new BigDecimal("12"); tabela[2][12]=new BigDecimal("12"); tabela[2][20]=new BigDecimal("12"); tabela[2][27]=new BigDecimal("4");
		// AP - AMAPA - 3
		tabela[3][0]=new BigDecimal("12"); tabela[3][7]=new BigDecimal("12"); tabela[3][14]=new BigDecimal("12"); tabela[3][21]=new BigDecimal("12");
		tabela[3][1]=new BigDecimal("12"); tabela[3][8]=new BigDecimal("12"); tabela[3][15]=new BigDecimal("12"); tabela[3][22]=new BigDecimal("12");
		tabela[3][2]=new BigDecimal("12"); tabela[3][9]=new BigDecimal("12"); tabela[3][16]=new BigDecimal("12"); tabela[3][23]=new BigDecimal("12");
		tabela[3][3]=new BigDecimal("18"); tabela[3][10]=new BigDecimal("12"); tabela[3][17]=new BigDecimal("12"); tabela[3][24]=new BigDecimal("12");
		tabela[3][4]=new BigDecimal("12"); tabela[3][11]=new BigDecimal("12"); tabela[3][18]=new BigDecimal("12"); tabela[3][25]=new BigDecimal("12");
		tabela[3][5]=new BigDecimal("12"); tabela[3][12]=new BigDecimal("12"); tabela[3][19]=new BigDecimal("12"); tabela[3][26]=new BigDecimal("12");
		tabela[3][6]=new BigDecimal("12"); tabela[3][12]=new BigDecimal("12"); tabela[3][20]=new BigDecimal("12"); tabela[3][27]=new BigDecimal("4");
		// BA - BAHIA - 4
		tabela[4][0]=new BigDecimal("12"); tabela[4][7]=new BigDecimal("12"); tabela[4][14]=new BigDecimal("12"); tabela[4][21]=new BigDecimal("12");
		tabela[4][1]=new BigDecimal("12"); tabela[4][8]=new BigDecimal("12"); tabela[4][15]=new BigDecimal("12"); tabela[4][22]=new BigDecimal("12");
		tabela[4][2]=new BigDecimal("12"); tabela[4][9]=new BigDecimal("12"); tabela[4][16]=new BigDecimal("12"); tabela[4][23]=new BigDecimal("12");
		tabela[4][3]=new BigDecimal("12"); tabela[4][10]=new BigDecimal("12"); tabela[4][17]=new BigDecimal("12"); tabela[4][24]=new BigDecimal("12");
		tabela[4][4]=new BigDecimal("18"); tabela[4][11]=new BigDecimal("12"); tabela[4][18]=new BigDecimal("12"); tabela[4][25]=new BigDecimal("12");
		tabela[4][5]=new BigDecimal("12"); tabela[4][12]=new BigDecimal("12"); tabela[4][19]=new BigDecimal("12"); tabela[4][26]=new BigDecimal("12");
		tabela[4][6]=new BigDecimal("12"); tabela[4][12]=new BigDecimal("12"); tabela[4][20]=new BigDecimal("12"); tabela[4][27]=new BigDecimal("4");
		// CE - CEARA - 5
		tabela[5][0]=new BigDecimal("12"); tabela[5][7]=new BigDecimal("12"); tabela[5][14]=new BigDecimal("12"); tabela[5][21]=new BigDecimal("12");
		tabela[5][1]=new BigDecimal("12"); tabela[5][8]=new BigDecimal("12"); tabela[5][15]=new BigDecimal("12"); tabela[5][22]=new BigDecimal("12");
		tabela[5][2]=new BigDecimal("12"); tabela[5][9]=new BigDecimal("12"); tabela[5][16]=new BigDecimal("12"); tabela[5][23]=new BigDecimal("12");
		tabela[5][3]=new BigDecimal("12"); tabela[5][10]=new BigDecimal("12"); tabela[5][17]=new BigDecimal("12"); tabela[5][24]=new BigDecimal("12");
		tabela[5][4]=new BigDecimal("12"); tabela[5][11]=new BigDecimal("12"); tabela[5][18]=new BigDecimal("12"); tabela[5][25]=new BigDecimal("12");
		tabela[5][5]=new BigDecimal("17"); tabela[5][12]=new BigDecimal("12"); tabela[5][19]=new BigDecimal("12"); tabela[5][26]=new BigDecimal("12");
		tabela[5][6]=new BigDecimal("12"); tabela[5][12]=new BigDecimal("12"); tabela[5][20]=new BigDecimal("12"); tabela[5][27]=new BigDecimal("4");
		// DF - DISTRITO FEDERAL - 6
		tabela[6][0]=new BigDecimal("12"); tabela[6][7]=new BigDecimal("12"); tabela[6][14]=new BigDecimal("12"); tabela[6][21]=new BigDecimal("12");
		tabela[6][1]=new BigDecimal("12"); tabela[6][8]=new BigDecimal("12"); tabela[6][15]=new BigDecimal("12"); tabela[6][22]=new BigDecimal("12");
		tabela[6][2]=new BigDecimal("12"); tabela[6][9]=new BigDecimal("12"); tabela[6][16]=new BigDecimal("12"); tabela[6][23]=new BigDecimal("12");
		tabela[6][3]=new BigDecimal("12"); tabela[6][10]=new BigDecimal("12"); tabela[6][17]=new BigDecimal("12"); tabela[6][24]=new BigDecimal("12");
		tabela[6][4]=new BigDecimal("12"); tabela[6][11]=new BigDecimal("12"); tabela[6][18]=new BigDecimal("12"); tabela[6][25]=new BigDecimal("12");
		tabela[6][5]=new BigDecimal("12"); tabela[6][12]=new BigDecimal("12"); tabela[6][19]=new BigDecimal("12"); tabela[6][26]=new BigDecimal("12");
		tabela[6][6]=new BigDecimal("18"); tabela[6][12]=new BigDecimal("12"); tabela[6][20]=new BigDecimal("12"); tabela[6][27]=new BigDecimal("4");
		// ES - ESPIRITO SANTO - 7
		tabela[7][0]=new BigDecimal("12"); tabela[7][7]=new BigDecimal("17"); tabela[7][14]=new BigDecimal("12"); tabela[7][21]=new BigDecimal("12");
		tabela[7][1]=new BigDecimal("12"); tabela[7][8]=new BigDecimal("12"); tabela[7][15]=new BigDecimal("12"); tabela[7][22]=new BigDecimal("12");
		tabela[7][2]=new BigDecimal("12"); tabela[7][9]=new BigDecimal("12"); tabela[7][16]=new BigDecimal("12"); tabela[7][23]=new BigDecimal("12");
		tabela[7][3]=new BigDecimal("12"); tabela[7][10]=new BigDecimal("12"); tabela[7][17]=new BigDecimal("12"); tabela[7][24]=new BigDecimal("12");
		tabela[7][4]=new BigDecimal("12"); tabela[7][11]=new BigDecimal("12"); tabela[7][18]=new BigDecimal("12"); tabela[7][25]=new BigDecimal("12");
		tabela[7][5]=new BigDecimal("12"); tabela[7][12]=new BigDecimal("12"); tabela[7][19]=new BigDecimal("12"); tabela[7][26]=new BigDecimal("12");
		tabela[7][6]=new BigDecimal("12"); tabela[7][12]=new BigDecimal("12"); tabela[7][20]=new BigDecimal("12"); tabela[7][27]=new BigDecimal("4");
		// GO - GOIAS - 8
		tabela[8][0]=new BigDecimal("12"); tabela[8][7]=new BigDecimal("12"); tabela[8][14]=new BigDecimal("12"); tabela[8][21]=new BigDecimal("12");
		tabela[8][1]=new BigDecimal("12"); tabela[8][8]=new BigDecimal("17"); tabela[8][15]=new BigDecimal("12"); tabela[8][22]=new BigDecimal("12");
		tabela[8][2]=new BigDecimal("12"); tabela[8][9]=new BigDecimal("12"); tabela[8][16]=new BigDecimal("12"); tabela[8][23]=new BigDecimal("12");
		tabela[8][3]=new BigDecimal("12"); tabela[8][10]=new BigDecimal("12"); tabela[8][17]=new BigDecimal("12"); tabela[8][24]=new BigDecimal("12");
		tabela[8][4]=new BigDecimal("12"); tabela[8][11]=new BigDecimal("12"); tabela[8][18]=new BigDecimal("12"); tabela[8][25]=new BigDecimal("12");
		tabela[8][5]=new BigDecimal("12"); tabela[8][12]=new BigDecimal("12"); tabela[8][19]=new BigDecimal("12"); tabela[8][26]=new BigDecimal("12");
		tabela[8][6]=new BigDecimal("12"); tabela[8][12]=new BigDecimal("12"); tabela[8][20]=new BigDecimal("12"); tabela[8][27]=new BigDecimal("4");
		// MA - MARANHAO - 9
		tabela[9][0]=new BigDecimal("12"); tabela[9][7]=new BigDecimal("12"); tabela[9][14]=new BigDecimal("12"); tabela[9][21]=new BigDecimal("12");
		tabela[9][1]=new BigDecimal("12"); tabela[9][8]=new BigDecimal("12"); tabela[9][15]=new BigDecimal("12"); tabela[9][22]=new BigDecimal("12");
		tabela[9][2]=new BigDecimal("12"); tabela[9][9]=new BigDecimal("18"); tabela[9][16]=new BigDecimal("12"); tabela[9][23]=new BigDecimal("12");
		tabela[9][3]=new BigDecimal("12"); tabela[9][10]=new BigDecimal("12"); tabela[9][17]=new BigDecimal("12"); tabela[9][24]=new BigDecimal("12");
		tabela[9][4]=new BigDecimal("12"); tabela[9][11]=new BigDecimal("12"); tabela[9][18]=new BigDecimal("12"); tabela[9][25]=new BigDecimal("12");
		tabela[9][5]=new BigDecimal("12"); tabela[9][12]=new BigDecimal("12"); tabela[9][19]=new BigDecimal("12"); tabela[9][26]=new BigDecimal("12");
		tabela[9][6]=new BigDecimal("12"); tabela[9][12]=new BigDecimal("12"); tabela[9][20]=new BigDecimal("12"); tabela[9][27]=new BigDecimal("4");
		// MT - MATO GROSSO - 10 
		tabela[10][0]=new BigDecimal("12"); tabela[10][7]=new BigDecimal("12"); tabela[10][14]=new BigDecimal("12"); tabela[10][21]=new BigDecimal("12");
		tabela[10][1]=new BigDecimal("12"); tabela[10][8]=new BigDecimal("12"); tabela[10][15]=new BigDecimal("12"); tabela[10][22]=new BigDecimal("12");
		tabela[10][2]=new BigDecimal("12"); tabela[10][9]=new BigDecimal("12"); tabela[10][16]=new BigDecimal("12"); tabela[10][23]=new BigDecimal("12");
		tabela[10][3]=new BigDecimal("12"); tabela[10][10]=new BigDecimal("17"); tabela[10][17]=new BigDecimal("12"); tabela[10][24]=new BigDecimal("12");
		tabela[10][4]=new BigDecimal("12"); tabela[10][11]=new BigDecimal("12"); tabela[10][18]=new BigDecimal("12"); tabela[10][25]=new BigDecimal("12");
		tabela[10][5]=new BigDecimal("12"); tabela[10][12]=new BigDecimal("12"); tabela[10][19]=new BigDecimal("12"); tabela[10][26]=new BigDecimal("12");
		tabela[10][6]=new BigDecimal("12"); tabela[10][12]=new BigDecimal("12"); tabela[10][20]=new BigDecimal("12"); tabela[10][27]=new BigDecimal("4");
		// MS - MATO GROSSO DO SUL - 11
		tabela[11][0]=new BigDecimal("12"); tabela[11][7]=new BigDecimal("12"); tabela[11][14]=new BigDecimal("12"); tabela[11][21]=new BigDecimal("12");
		tabela[11][1]=new BigDecimal("12"); tabela[11][8]=new BigDecimal("12"); tabela[11][15]=new BigDecimal("12"); tabela[11][22]=new BigDecimal("12");
		tabela[11][2]=new BigDecimal("12"); tabela[11][9]=new BigDecimal("12"); tabela[11][16]=new BigDecimal("12"); tabela[11][23]=new BigDecimal("12");
		tabela[11][3]=new BigDecimal("12"); tabela[11][10]=new BigDecimal("12"); tabela[11][17]=new BigDecimal("12"); tabela[11][24]=new BigDecimal("12");
		tabela[11][4]=new BigDecimal("12"); tabela[11][11]=new BigDecimal("17"); tabela[11][18]=new BigDecimal("12"); tabela[11][25]=new BigDecimal("12");
		tabela[11][5]=new BigDecimal("12"); tabela[11][12]=new BigDecimal("12"); tabela[11][19]=new BigDecimal("12"); tabela[11][26]=new BigDecimal("12");
		tabela[11][6]=new BigDecimal("12"); tabela[11][12]=new BigDecimal("12"); tabela[11][20]=new BigDecimal("12"); tabela[11][27]=new BigDecimal("4");
		// MG - MINAS GERAIS - 12
		tabela[12][0]=new BigDecimal("7"); tabela[12][7]=new BigDecimal("7"); tabela[12][14]=new BigDecimal("7"); tabela[12][21]=new BigDecimal("7");
		tabela[12][1]=new BigDecimal("7"); tabela[12][8]=new BigDecimal("7"); tabela[12][15]=new BigDecimal("12"); tabela[12][22]=new BigDecimal("7");
		tabela[12][2]=new BigDecimal("7"); tabela[12][9]=new BigDecimal("7"); tabela[12][16]=new BigDecimal("7"); tabela[12][23]=new BigDecimal("12");
		tabela[12][3]=new BigDecimal("7"); tabela[12][10]=new BigDecimal("7"); tabela[12][17]=new BigDecimal("7"); tabela[12][24]=new BigDecimal("12");
		tabela[12][4]=new BigDecimal("7"); tabela[12][11]=new BigDecimal("7"); tabela[12][18]=new BigDecimal("7"); tabela[12][25]=new BigDecimal("7");
		tabela[12][5]=new BigDecimal("7"); tabela[12][12]=new BigDecimal("18"); tabela[12][19]=new BigDecimal("12"); tabela[12][26]=new BigDecimal("7");
		tabela[12][6]=new BigDecimal("7"); tabela[12][13]=new BigDecimal("7"); tabela[12][20]=new BigDecimal("12"); tabela[12][27]=new BigDecimal("4");
		// PA - PARA - 13
		tabela[13][0]=new BigDecimal("12"); tabela[13][7]=new BigDecimal("12"); tabela[13][14]=new BigDecimal("12"); tabela[13][21]=new BigDecimal("12");
		tabela[13][1]=new BigDecimal("12"); tabela[13][8]=new BigDecimal("12"); tabela[13][15]=new BigDecimal("12"); tabela[13][22]=new BigDecimal("12");
		tabela[13][2]=new BigDecimal("12"); tabela[13][9]=new BigDecimal("12"); tabela[13][16]=new BigDecimal("12"); tabela[13][23]=new BigDecimal("12");
		tabela[13][3]=new BigDecimal("12"); tabela[13][10]=new BigDecimal("12"); tabela[13][17]=new BigDecimal("12"); tabela[13][24]=new BigDecimal("12");
		tabela[13][4]=new BigDecimal("12"); tabela[13][11]=new BigDecimal("12"); tabela[13][18]=new BigDecimal("12"); tabela[13][25]=new BigDecimal("12");
		tabela[13][5]=new BigDecimal("12"); tabela[13][12]=new BigDecimal("12"); tabela[13][19]=new BigDecimal("12"); tabela[13][26]=new BigDecimal("12");
		tabela[13][6]=new BigDecimal("12"); tabela[13][13]=new BigDecimal("17"); tabela[13][20]=new BigDecimal("12"); tabela[13][27]=new BigDecimal("4");
		// PB - PARAIBA - 14
		tabela[14][0]=new BigDecimal("12"); tabela[14][7]=new BigDecimal("12"); tabela[14][14]=new BigDecimal("18"); tabela[14][21]=new BigDecimal("12");
		tabela[14][1]=new BigDecimal("12"); tabela[14][8]=new BigDecimal("12"); tabela[14][15]=new BigDecimal("12"); tabela[14][22]=new BigDecimal("12");
		tabela[14][2]=new BigDecimal("12"); tabela[14][9]=new BigDecimal("12"); tabela[14][16]=new BigDecimal("12"); tabela[14][23]=new BigDecimal("12");
		tabela[14][3]=new BigDecimal("12"); tabela[14][10]=new BigDecimal("12"); tabela[14][17]=new BigDecimal("12"); tabela[14][24]=new BigDecimal("12");
		tabela[14][4]=new BigDecimal("12"); tabela[14][11]=new BigDecimal("12"); tabela[14][18]=new BigDecimal("12"); tabela[14][25]=new BigDecimal("12");
		tabela[14][5]=new BigDecimal("12"); tabela[14][12]=new BigDecimal("12"); tabela[14][19]=new BigDecimal("12"); tabela[14][26]=new BigDecimal("12");
		tabela[14][6]=new BigDecimal("12"); tabela[14][13]=new BigDecimal("12"); tabela[14][20]=new BigDecimal("12"); tabela[14][27]=new BigDecimal("4");
		// PR - PARANA - 15
		tabela[15][0]=new BigDecimal("7"); tabela[15][7]=new BigDecimal("7"); tabela[15][14]=new BigDecimal("7"); tabela[15][21]=new BigDecimal("7");
		tabela[15][1]=new BigDecimal("7"); tabela[15][8]=new BigDecimal("7"); tabela[15][15]=new BigDecimal("18"); tabela[15][22]=new BigDecimal("7");
		tabela[15][2]=new BigDecimal("7"); tabela[15][9]=new BigDecimal("7"); tabela[15][16]=new BigDecimal("7"); tabela[15][23]=new BigDecimal("12");
		tabela[15][3]=new BigDecimal("7"); tabela[15][10]=new BigDecimal("7"); tabela[15][17]=new BigDecimal("7"); tabela[15][24]=new BigDecimal("12");
		tabela[15][4]=new BigDecimal("7"); tabela[15][11]=new BigDecimal("7"); tabela[15][18]=new BigDecimal("7"); tabela[15][25]=new BigDecimal("7");
		tabela[15][5]=new BigDecimal("7"); tabela[15][12]=new BigDecimal("12"); tabela[15][19]=new BigDecimal("12"); tabela[15][26]=new BigDecimal("7");
		tabela[15][6]=new BigDecimal("7"); tabela[15][13]=new BigDecimal("7"); tabela[15][20]=new BigDecimal("12"); tabela[15][27]=new BigDecimal("4");
		// PE - PERNAMBUCO - 16
		tabela[16][0]=new BigDecimal("12"); tabela[16][7]=new BigDecimal("12"); tabela[16][14]=new BigDecimal("12"); tabela[16][21]=new BigDecimal("12");
		tabela[16][1]=new BigDecimal("12"); tabela[16][8]=new BigDecimal("12"); tabela[16][15]=new BigDecimal("12"); tabela[16][22]=new BigDecimal("12");
		tabela[16][2]=new BigDecimal("12"); tabela[16][9]=new BigDecimal("12"); tabela[16][16]=new BigDecimal("18"); tabela[16][23]=new BigDecimal("12");
		tabela[16][3]=new BigDecimal("12"); tabela[16][10]=new BigDecimal("12"); tabela[16][17]=new BigDecimal("12"); tabela[16][24]=new BigDecimal("12");
		tabela[16][4]=new BigDecimal("12"); tabela[16][11]=new BigDecimal("12"); tabela[16][18]=new BigDecimal("12"); tabela[16][25]=new BigDecimal("12");
		tabela[16][5]=new BigDecimal("12"); tabela[16][12]=new BigDecimal("12"); tabela[16][19]=new BigDecimal("12"); tabela[16][26]=new BigDecimal("12");
		tabela[16][6]=new BigDecimal("12"); tabela[16][13]=new BigDecimal("12"); tabela[16][20]=new BigDecimal("12"); tabela[16][27]=new BigDecimal("4");
		// PI - PIAUI - 17
		tabela[17][0]=new BigDecimal("12"); tabela[17][7]=new BigDecimal("12"); tabela[17][14]=new BigDecimal("12"); tabela[17][21]=new BigDecimal("12");
		tabela[17][1]=new BigDecimal("12"); tabela[17][8]=new BigDecimal("12"); tabela[17][15]=new BigDecimal("12"); tabela[17][22]=new BigDecimal("12");
		tabela[17][2]=new BigDecimal("12"); tabela[17][9]=new BigDecimal("12"); tabela[17][16]=new BigDecimal("12"); tabela[17][23]=new BigDecimal("12");
		tabela[17][3]=new BigDecimal("12"); tabela[17][10]=new BigDecimal("12"); tabela[17][17]=new BigDecimal("17"); tabela[17][24]=new BigDecimal("12");
		tabela[17][4]=new BigDecimal("12"); tabela[17][11]=new BigDecimal("12"); tabela[17][18]=new BigDecimal("12"); tabela[17][25]=new BigDecimal("12");
		tabela[17][5]=new BigDecimal("12"); tabela[17][12]=new BigDecimal("12"); tabela[17][19]=new BigDecimal("12"); tabela[17][26]=new BigDecimal("12");
		tabela[17][6]=new BigDecimal("12"); tabela[17][13]=new BigDecimal("12"); tabela[17][20]=new BigDecimal("12"); tabela[17][27]=new BigDecimal("4");
		// RN - RIO GRANDE DO NORTE -18
		tabela[18][0]=new BigDecimal("12"); tabela[18][7]=new BigDecimal("12"); tabela[18][14]=new BigDecimal("12"); tabela[18][21]=new BigDecimal("12");
		tabela[18][1]=new BigDecimal("12"); tabela[18][8]=new BigDecimal("12"); tabela[18][15]=new BigDecimal("12"); tabela[18][22]=new BigDecimal("12");
		tabela[18][2]=new BigDecimal("12"); tabela[18][9]=new BigDecimal("12"); tabela[18][16]=new BigDecimal("12"); tabela[18][23]=new BigDecimal("12");
		tabela[18][3]=new BigDecimal("12"); tabela[18][10]=new BigDecimal("12"); tabela[18][17]=new BigDecimal("12"); tabela[18][24]=new BigDecimal("12");
		tabela[18][4]=new BigDecimal("12"); tabela[18][11]=new BigDecimal("12"); tabela[18][18]=new BigDecimal("18"); tabela[18][25]=new BigDecimal("12");
		tabela[18][5]=new BigDecimal("12"); tabela[18][12]=new BigDecimal("12"); tabela[18][19]=new BigDecimal("12"); tabela[18][26]=new BigDecimal("12");
		tabela[18][6]=new BigDecimal("12"); tabela[18][13]=new BigDecimal("12"); tabela[18][20]=new BigDecimal("12"); tabela[18][27]=new BigDecimal("4");
		// RS - RIO GRANDE DO SUL - 19 
		tabela[19][0]=new BigDecimal("7"); tabela[19][7]=new BigDecimal("7"); tabela[19][14]=new BigDecimal("7"); tabela[19][21]=new BigDecimal("7");
		tabela[19][1]=new BigDecimal("7"); tabela[19][8]=new BigDecimal("7"); tabela[19][15]=new BigDecimal("12"); tabela[19][22]=new BigDecimal("7");
		tabela[19][2]=new BigDecimal("7"); tabela[19][9]=new BigDecimal("7"); tabela[19][16]=new BigDecimal("7"); tabela[19][23]=new BigDecimal("12");
		tabela[19][3]=new BigDecimal("7"); tabela[19][10]=new BigDecimal("7"); tabela[19][17]=new BigDecimal("7"); tabela[19][24]=new BigDecimal("12");
		tabela[19][4]=new BigDecimal("7"); tabela[19][11]=new BigDecimal("7"); tabela[19][18]=new BigDecimal("7"); tabela[19][25]=new BigDecimal("7");
		tabela[19][5]=new BigDecimal("7"); tabela[19][12]=new BigDecimal("12"); tabela[19][19]=new BigDecimal("18"); tabela[19][26]=new BigDecimal("7");
		tabela[19][6]=new BigDecimal("7"); tabela[19][13]=new BigDecimal("7"); tabela[19][20]=new BigDecimal("12"); tabela[19][27]=new BigDecimal("4");
		// RJ - RIO DE JANEIRO - 20
		tabela[20][0]=new BigDecimal("7"); tabela[20][7]=new BigDecimal("7"); tabela[20][14]=new BigDecimal("7"); tabela[20][21]=new BigDecimal("7");
		tabela[20][1]=new BigDecimal("7"); tabela[20][8]=new BigDecimal("7"); tabela[20][15]=new BigDecimal("12"); tabela[20][22]=new BigDecimal("7");
		tabela[20][2]=new BigDecimal("7"); tabela[20][9]=new BigDecimal("7"); tabela[20][16]=new BigDecimal("7"); tabela[20][23]=new BigDecimal("12");
		tabela[20][3]=new BigDecimal("7"); tabela[20][10]=new BigDecimal("7"); tabela[20][17]=new BigDecimal("7"); tabela[20][24]=new BigDecimal("12");
		tabela[20][4]=new BigDecimal("7"); tabela[20][11]=new BigDecimal("7"); tabela[20][18]=new BigDecimal("7"); tabela[20][25]=new BigDecimal("7");
		tabela[20][5]=new BigDecimal("7"); tabela[20][12]=new BigDecimal("12"); tabela[20][19]=new BigDecimal("12"); tabela[20][26]=new BigDecimal("7");
		tabela[20][6]=new BigDecimal("7"); tabela[20][13]=new BigDecimal("7"); tabela[20][20]=new BigDecimal("18"); tabela[20][27]=new BigDecimal("4");
		// RO - RONDONIA - 21
		tabela[21][0]=new BigDecimal("12"); tabela[21][7]=new BigDecimal("12"); tabela[21][14]=new BigDecimal("12"); tabela[21][21]=new BigDecimal("17");
		tabela[21][1]=new BigDecimal("12"); tabela[21][8]=new BigDecimal("12"); tabela[21][15]=new BigDecimal("12"); tabela[21][22]=new BigDecimal("12");
		tabela[21][2]=new BigDecimal("12"); tabela[21][9]=new BigDecimal("12"); tabela[21][16]=new BigDecimal("12"); tabela[21][23]=new BigDecimal("12");
		tabela[21][3]=new BigDecimal("12"); tabela[21][10]=new BigDecimal("12"); tabela[21][17]=new BigDecimal("12"); tabela[21][24]=new BigDecimal("12");
		tabela[21][4]=new BigDecimal("12"); tabela[21][11]=new BigDecimal("12"); tabela[21][18]=new BigDecimal("12"); tabela[21][25]=new BigDecimal("12");
		tabela[21][5]=new BigDecimal("12"); tabela[21][12]=new BigDecimal("12"); tabela[21][19]=new BigDecimal("12"); tabela[21][26]=new BigDecimal("12");
		tabela[21][6]=new BigDecimal("12"); tabela[21][13]=new BigDecimal("12"); tabela[21][20]=new BigDecimal("12"); tabela[21][27]=new BigDecimal("4");
		// RR - RORAIMA - 22
		tabela[22][0]=new BigDecimal("12"); tabela[22][7]=new BigDecimal("12"); tabela[22][14]=new BigDecimal("12"); tabela[22][21]=new BigDecimal("12");
		tabela[22][1]=new BigDecimal("12"); tabela[22][8]=new BigDecimal("12"); tabela[22][15]=new BigDecimal("12"); tabela[22][22]=new BigDecimal("17");
		tabela[22][2]=new BigDecimal("12"); tabela[22][9]=new BigDecimal("12"); tabela[22][16]=new BigDecimal("12"); tabela[22][23]=new BigDecimal("12");
		tabela[22][3]=new BigDecimal("12"); tabela[22][10]=new BigDecimal("12"); tabela[22][17]=new BigDecimal("12"); tabela[22][24]=new BigDecimal("12");
		tabela[22][4]=new BigDecimal("12"); tabela[22][11]=new BigDecimal("12"); tabela[22][18]=new BigDecimal("12"); tabela[22][25]=new BigDecimal("12");
		tabela[22][5]=new BigDecimal("12"); tabela[22][12]=new BigDecimal("12"); tabela[22][19]=new BigDecimal("12"); tabela[22][26]=new BigDecimal("12");
		tabela[22][6]=new BigDecimal("12"); tabela[22][13]=new BigDecimal("12"); tabela[22][20]=new BigDecimal("12"); tabela[22][27]=new BigDecimal("4");
		// SC - SANTA CATARINA - 23 
		tabela[23][0]=new BigDecimal("7"); tabela[23][7]=new BigDecimal("7"); tabela[23][14]=new BigDecimal("7"); tabela[23][21]=new BigDecimal("7");
		tabela[23][1]=new BigDecimal("7"); tabela[23][8]=new BigDecimal("7"); tabela[23][15]=new BigDecimal("12"); tabela[23][22]=new BigDecimal("7");
		tabela[23][2]=new BigDecimal("7"); tabela[23][9]=new BigDecimal("7"); tabela[23][16]=new BigDecimal("7"); tabela[23][23]=new BigDecimal("17");
		tabela[23][3]=new BigDecimal("7"); tabela[23][10]=new BigDecimal("7"); tabela[23][17]=new BigDecimal("7"); tabela[23][24]=new BigDecimal("12");
		tabela[23][4]=new BigDecimal("7"); tabela[23][11]=new BigDecimal("7"); tabela[23][18]=new BigDecimal("7"); tabela[23][25]=new BigDecimal("7");
		tabela[23][5]=new BigDecimal("7"); tabela[23][12]=new BigDecimal("12"); tabela[23][19]=new BigDecimal("12"); tabela[23][26]=new BigDecimal("7");
		tabela[23][6]=new BigDecimal("7"); tabela[23][13]=new BigDecimal("7"); tabela[23][20]=new BigDecimal("12"); tabela[23][27]=new BigDecimal("4");
		// SP - SAO PAULO - 24
		tabela[24][0]=new BigDecimal("7"); tabela[24][7]=new BigDecimal("7"); tabela[24][14]=new BigDecimal("7"); tabela[24][21]=new BigDecimal("7");
		tabela[24][1]=new BigDecimal("7"); tabela[24][8]=new BigDecimal("7"); tabela[24][15]=new BigDecimal("12"); tabela[24][22]=new BigDecimal("7");
		tabela[24][2]=new BigDecimal("7"); tabela[24][9]=new BigDecimal("7"); tabela[24][16]=new BigDecimal("7"); tabela[24][23]=new BigDecimal("12");
		tabela[24][3]=new BigDecimal("7"); tabela[24][10]=new BigDecimal("7"); tabela[24][17]=new BigDecimal("7"); tabela[24][24]=new BigDecimal("18");
		tabela[24][4]=new BigDecimal("7"); tabela[24][11]=new BigDecimal("7"); tabela[24][18]=new BigDecimal("7"); tabela[24][25]=new BigDecimal("7");
		tabela[24][5]=new BigDecimal("7"); tabela[24][12]=new BigDecimal("12"); tabela[24][19]=new BigDecimal("12"); tabela[24][26]=new BigDecimal("7");
		tabela[24][6]=new BigDecimal("7"); tabela[24][13]=new BigDecimal("7"); tabela[24][20]=new BigDecimal("12"); tabela[24][27]=new BigDecimal("4");
		// SE - SERGIPE - 25
		tabela[25][0]=new BigDecimal("12"); tabela[25][7]=new BigDecimal("12"); tabela[25][14]=new BigDecimal("12"); tabela[25][21]=new BigDecimal("12");
		tabela[25][1]=new BigDecimal("12"); tabela[25][8]=new BigDecimal("12"); tabela[25][15]=new BigDecimal("12"); tabela[25][22]=new BigDecimal("12");
		tabela[25][2]=new BigDecimal("12"); tabela[25][9]=new BigDecimal("12"); tabela[25][16]=new BigDecimal("12"); tabela[25][23]=new BigDecimal("12");
		tabela[25][3]=new BigDecimal("12"); tabela[25][10]=new BigDecimal("12"); tabela[25][17]=new BigDecimal("12"); tabela[25][24]=new BigDecimal("12");
		tabela[25][4]=new BigDecimal("12"); tabela[25][11]=new BigDecimal("12"); tabela[25][18]=new BigDecimal("12"); tabela[25][25]=new BigDecimal("18");
		tabela[25][5]=new BigDecimal("12"); tabela[25][12]=new BigDecimal("12"); tabela[25][19]=new BigDecimal("12"); tabela[25][26]=new BigDecimal("12");
		tabela[25][6]=new BigDecimal("12"); tabela[25][13]=new BigDecimal("12"); tabela[25][20]=new BigDecimal("12"); tabela[25][27]=new BigDecimal("4");
		// TO - TOCANTINS - 26
		tabela[26][0]=new BigDecimal("12"); tabela[26][7]=new BigDecimal("12"); tabela[26][14]=new BigDecimal("12"); tabela[26][21]=new BigDecimal("12");
		tabela[26][1]=new BigDecimal("12"); tabela[26][8]=new BigDecimal("12"); tabela[26][15]=new BigDecimal("12"); tabela[26][22]=new BigDecimal("12");
		tabela[26][2]=new BigDecimal("12"); tabela[26][9]=new BigDecimal("12"); tabela[26][16]=new BigDecimal("12"); tabela[26][23]=new BigDecimal("12");
		tabela[26][3]=new BigDecimal("12"); tabela[26][10]=new BigDecimal("12"); tabela[26][17]=new BigDecimal("12"); tabela[26][24]=new BigDecimal("12");
		tabela[26][4]=new BigDecimal("12"); tabela[26][11]=new BigDecimal("12"); tabela[26][18]=new BigDecimal("12"); tabela[26][25]=new BigDecimal("12");
		tabela[26][5]=new BigDecimal("12"); tabela[26][12]=new BigDecimal("12"); tabela[26][19]=new BigDecimal("12"); tabela[26][26]=new BigDecimal("18");
		tabela[26][6]=new BigDecimal("12"); tabela[26][13]=new BigDecimal("12"); tabela[26][20]=new BigDecimal("12"); tabela[26][27]=new BigDecimal("4");
		// EX - EXTERIOR - 27
		tabela[27][0]=new BigDecimal("4"); tabela[27][7]=new BigDecimal("4"); tabela[27][14]=new BigDecimal("4"); tabela[27][21]=new BigDecimal("4");
		tabela[27][1]=new BigDecimal("4"); tabela[27][8]=new BigDecimal("4"); tabela[27][15]=new BigDecimal("4"); tabela[27][22]=new BigDecimal("4");
		tabela[27][2]=new BigDecimal("4"); tabela[27][9]=new BigDecimal("4"); tabela[27][16]=new BigDecimal("4"); tabela[27][23]=new BigDecimal("4");
		tabela[27][3]=new BigDecimal("4"); tabela[27][10]=new BigDecimal("4"); tabela[27][17]=new BigDecimal("4"); tabela[27][24]=new BigDecimal("4");
		tabela[27][4]=new BigDecimal("4"); tabela[27][11]=new BigDecimal("4"); tabela[27][18]=new BigDecimal("4"); tabela[27][25]=new BigDecimal("4");
		tabela[27][5]=new BigDecimal("4"); tabela[27][12]=new BigDecimal("4"); tabela[27][19]=new BigDecimal("4"); tabela[27][26]=new BigDecimal("4");
		tabela[27][6]=new BigDecimal("4"); tabela[27][13]=new BigDecimal("4"); tabela[27][20]=new BigDecimal("4"); tabela[27][27]=new BigDecimal("4");
		
	}
	public BigDecimal pegaAliquota(Uf origem, Uf destino){
		return tabela[origem.getCod()][destino.getCod()];
	}
}
