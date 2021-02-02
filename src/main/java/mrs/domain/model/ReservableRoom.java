package mrs.domain.model;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;


//特定の日に予約可能な会議室の情報を格納するテーブル
@Entity
public class ReservableRoom implements Serializable{

	/*
	 * 複合キーを扱うために複合主キークラスの
	 * ReservableRoomIdを用意し@EmbeddedIdを付与
	 */
	@EmbeddedId
	private ReservableRoomId reservableRoomId; //roomId, reservedDate 複合キー

	/*
	 * MeetingRoomエンティティと多対1の関連設定。
	 * 外部キーとしてroom_idを指定する。ただし、このフィールドは関連を表すために存在し、
	 * 実際のroom_idに対応するフィールドはReservableRoomIdクラスが持つ。そのため、このフィールドに
	 * 対する値の変更がデータベースに反映されないようinsertable・updatableの属性をfalseにする
	 */
	@ManyToOne
	@JoinColumn(name = "room_id", insertable = false, updatable = false)
	@MapsId("roomId") //外部キーとして使いフィールド名を指定
	private MeetingRoom meetingRoom;

	public ReservableRoom(ReservableRoomId reservableRoomId) {
		this.reservableRoomId = reservableRoomId;
	}

	public ReservableRoom() {

	}

	public MeetingRoom getMeetingRoom() {
		return meetingRoom;
	}

	public void setMeetingRoom(MeetingRoom meetingRoom) {
		this.meetingRoom = meetingRoom;
	}

	public ReservableRoomId getReservableRoomId() {
		return reservableRoomId;
	}

	public void setReservableRoomId(ReservableRoomId reservableRoomId) {
		this.reservableRoomId = reservableRoomId;
	}
}
