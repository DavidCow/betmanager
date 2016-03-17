package bettingBot.entities;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import jayeson.lib.datastructure.BasicSoccerEvent;
import jayeson.lib.datastructure.LBType;
import jayeson.lib.datastructure.MetaRecord;
import jayeson.lib.datastructure.OddType;
import jayeson.lib.datastructure.PivotType;
import jayeson.lib.datastructure.Record;
import jayeson.lib.datastructure.SoccerEvent;
import jayeson.lib.datastructure.SpecialEventCategory;
import jayeson.lib.datastructure.TimeType;

import com.google.gson.InstanceCreator;

public class ExtendedSoccerEventInstanceCreator implements InstanceCreator<SoccerEvent>{
	
	@Override
	public SoccerEvent createInstance(Type arg0) {
		return new BasicSoccerEvent() {
			
			@Override
			public boolean hasTimeType(TimeType arg0) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean hasPivotType(PivotType arg0) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean hasOddType(OddType arg0) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean hasLBType(LBType arg0) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public SpecialEventCategory getSpecialEventCategory() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public List<MetaRecord> getRecordsFromOddId(int arg0) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Collection<Record> getRecords(List<String> arg0, LBType arg1) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Collection<Record> getRecords(String arg0, LBType arg1) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Collection<Record> getRecords(String arg0, PivotType arg1) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Collection<Record> getRecords(String arg0, OddType arg1) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Collection<Record> getRecords(String arg0, TimeType arg1) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Record getRecords(int arg0) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Collection<Record> getRecords(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Collection<Record> getRecords() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Record getRecordFromOddId(String arg0, OddType arg1, Integer arg2) {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}
}
