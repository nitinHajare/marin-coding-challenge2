package com.marin.model;

public class Sansad {
	int sno, division, loksabha, session, totalSittings, attendance;
	String memberName, state, constituency;

	public int getSno() {
		return sno;
	}

	public void setSno(int sno) {
		this.sno = sno;
	}

	public int getDivision() {
		return division;
	}

	public void setDivision(int division) {
		this.division = division;
	}

	public int getLoksabha() {
		return loksabha;
	}

	public void setLoksabha(int loksabha) {
		this.loksabha = loksabha;
	}

	public int getSession() {
		return session;
	}

	public void setSession(int session) {
		this.session = session;
	}

	public int getTotalSittings() {
		return totalSittings;
	}

	public void setTotalSittings(int totalSittings) {
		this.totalSittings = totalSittings;
	}

	public int getAttendance() {
		return attendance;
	}

	public void setAttendance(int attendance) {
		this.attendance = attendance;
	}

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getConstituency() {
		return constituency;
	}

	public void setConstituency(String constituency) {
		this.constituency = constituency;
	}
}
