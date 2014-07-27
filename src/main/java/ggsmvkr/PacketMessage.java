package ggsmvkr;

import java.util.Date;

public class PacketMessage {

		Date date;
		int id;
		
//  	B1
//		bit7 Электропитание в норме
//		bit6 Электропитание датчиков в норме
//		bit5 Состояние расходомера №1
//		bit4 Состояние расходомера №2
//		bit3 Состояние расходомера №3
//		bit2 Обобщенный сигнал Авария
//		bit1 Резерв
//		bit0 Резерв

		boolean power;
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

		public boolean isPower() {
			return power;
		}

		public void setPower(boolean power) {
			this.power = power;
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

	
}
