package com.intechua.db.beans;

import java.util.Date;

public class PacketEntry {


	@Override
	public String toString()
	{
		return "PacketEntry [date=" + date + ", id=" + id + ", power1=" + power1 + ", power2=" + power2 + ", power3=" + power3 + ", sensorPower=" + sensorPower
				+ ", flowmeterState1=" + flowmeterState1 + ", flowmeterState2=" + flowmeterState2 + ", flowmeterState3=" + flowmeterState3 + ", alert=" + alert + ", reserve1="
				+ reserve1 + ", reserve2=" + reserve2 + ", level1=" + level1 + ", level2=" + level2 + ", level3=" + level3 + "]";
	}

	Date date;
	int id;
	
//	B1
//	bit7 Электропитание в норме
//	bit6 Электропитание датчиков в норме
//	bit5 Состояние расходомера №1
//	bit4 Состояние расходомера №2
//	bit3 Состояние расходомера №3
//	bit2 Обобщенный сигнал Авария
//	bit1 Резерв
//	bit0 Резерв

	boolean power1;
	boolean power2;
	boolean power3;
	boolean sensorPower;
	boolean flowmeterState1;
	boolean flowmeterState2;
	boolean flowmeterState3;
	boolean alert;
	boolean reserve1;
	boolean reserve2;

	int level1;
	int level2;
	int level3;

	public boolean isPower1()
	{
		return power1;
	}

	public void setPower1(boolean power1)
	{
		this.power1 = power1;
	}

	public boolean isPower2()
	{
		return power2;
	}

	public void setPower2(boolean power2)
	{
		this.power2 = power2;
	}

	public boolean isPower3()
	{
		return power3;
	}

	public void setPower3(boolean power3)
	{
		this.power3 = power3;
	}
	
	
	public int getLevel1() {
		return level1;
	}

	public void setLevel1(int level1) {
		this.level1 = level1;
	}

	public int getLevel2() {
		return level2;
	}

	public void setLevel2(int level2) {
		this.level2 = level2;
	}

	public int getLevel3() {
		return level3;
	}

	public void setLevel3(int level3) {
		this.level3 = level3;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getDate() {
		return this.date;
	}

	public int getId() {
		return this.id;
	}

	public boolean isSensorPower() {
		return sensorPower;
	}

	public void setSensorPower(boolean sensorPower) {
		this.sensorPower = sensorPower;
	}

	public boolean isFlowmeterState1() {
		return flowmeterState1;
	}

	public void setFlowmeterState1(boolean flowmeterState1) {
		this.flowmeterState1 = flowmeterState1;
	}

	public boolean isFlowmeterState2() {
		return flowmeterState2;
	}

	public void setFlowmeterState2(boolean flowmeterState2) {
		this.flowmeterState2 = flowmeterState2;
	}

	public boolean isFlowmeterState3() {
		return flowmeterState3;
	}

	public void setFlowmeterState3(boolean flowmeterState3) {
		this.flowmeterState3 = flowmeterState3;
	}

	public boolean isAlert() {
		return alert;
	}

	public void setAlert(boolean alert) {
		this.alert = alert;
	}

	public boolean isReserve1() {
		return reserve1;
	}

	public void setReserve1(boolean reserve1) {
		this.reserve1 = reserve1;
	}

	public boolean isReserve2() {
		return reserve2;
	}

	public void setReserve2(boolean reserve2) {
		this.reserve2 = reserve2;
	}
	
	public static PacketEntry fromString(String strPacket)
	{
		PacketEntry pe = new PacketEntry();
		int intValue = Integer.parseInt(strPacket.substring(0, 2), 16);  
//		bit7 Электропитание в норме
//		bit6 Электропитание датчиков в норме
//		bit5 Состояние расходомера №1
//		bit4 Состояние расходомера №2
//		bit3 Состояние расходомера №3
//		bit2 Обобщенный сигнал Авария
//		bit1 Резерв
//		bit0 Резерв

		pe.setPower1( ((intValue & 0x80) > 0 ? true : false));
		pe.setPower2(pe.isPower1());
		pe.setPower3(pe.isPower1());
		pe.setSensorPower ( ((intValue & 0x40) > 0 ? true : false));
		pe.setFlowmeterState1 ( ((intValue & 0x20) > 0 ? true : false));
		pe.setFlowmeterState2 ( ((intValue & 0x10) > 0 ? true : false));
		pe.setFlowmeterState3 ( ((intValue & 0x08) > 0 ? true : false));
		pe.setAlert ( ((intValue & 0x04) > 0 ? true : false));
		pe.setReserve1 ( ((intValue & 0x02) > 0 ? true : false));
		pe.setReserve2 ( ((intValue & 0x01) > 0 ? true : false));
		
		pe.setLevel1 ( Integer.parseInt(strPacket.substring(2, 6), 16));
		pe.setLevel2 ( Integer.parseInt(strPacket.substring(6, 10), 16));
		pe.setLevel3 ( Integer.parseInt(strPacket.substring(10, 14), 16));
		
		return pe;
	}

}
