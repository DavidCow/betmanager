package bettingManager.gui;

import java.text.NumberFormat;
import java.util.List;
import java.util.Observable;

import bettingManager.statsCalculation.StatsRow;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;

public class TableKindOfBetController extends Observable{
	private MainController mainC;
	public static int OPTIONS_TABLEKINDOFBET_ID = 10;
	
	
	@FXML TableView<StatsRow> tableKindOfBet;
	private ObservableList<StatsRow> data;
	


	/**
	 * Initialize
	 */
	public void init(MainController mainC) {
		this.mainC = mainC;
		inflateTable(TableTitles.TABLE_TITLES_KINDOFBET);

		
		tableKindOfBet.getSelectionModel().setSelectionMode(
		    SelectionMode.MULTIPLE
		);
	}
	
	/**
	 * Populate Table with Column Headers
	 * @param tableTitles
	 */
	private void inflateTable(String [] tableTitles) {
		System.out.println("Inflating table...");
		tableKindOfBet.getColumns().clear();
		for(int i = 0; i<tableTitles.length; i+=1) {
			TableColumn<StatsRow, Object> newTC = new TableColumn<StatsRow, Object>(tableTitles[i]);
			//Backup on the bottom 
			newTC.setCellFactory(TextFieldTableCell.<StatsRow, Object>forTableColumn(new StringConverter<Object>() {
		    private final NumberFormat nf = NumberFormat.getNumberInstance();
		
		    {
		         nf.setMaximumFractionDigits(2);
		         nf.setMinimumFractionDigits(2);
		    }
		
		
		    @Override public String fromString(final String s) {
		        // Don't need this, unless table is editable, see DoubleStringConverter if needed
		        return null; 
		    }
		
			@Override
			public String toString(Object value) {
				try {
					if (value instanceof String) {
						return nf.format(Double.parseDouble((String) value));
					} else {
						return nf.format((Double) value);
					}
		    	} catch (NumberFormatException n) {
		    		return (String)value;
		    	}
			}
		}));
			newTC.setCellValueFactory(new PropertyValueFactory<StatsRow, Object>(TableTitles.kindOfBetTableValueNames[i]));
			tableKindOfBet.getColumns().add(newTC);
		}
		
		
		/**
		 * TEST TO FILL IN TABLES WITH DATA
		 */
		System.out.println("Reading KoB Data...");
		List<StatsRow> rows = this.mainC.getStatsCalc().getKoBStats();
		System.out.println("Reading KoB Data Done!");
		
//		TableValue tv = new TableValue();
//		tv.setKindOfBet(new String("works"));
//		tv.setAverageYield(new String("works"));
//		tv.setAverageOdds(new String("works"));
//		tv.setNumberOfBets(new String("works"));
//		tv.setPercentWeGet(new String("works"));
//		tv.setPercentOver95(new String("works"));
//		tv.setAverageLiquidity(new String("works"));
//		tv.setPercentOfTipsFound(new String("works"));
//		tv.setFlatStakeYield(new String("works"));
//		
//		TableValue tv2 = new TableValue();
//		tv2.setKindOfBet(new String("works2"));
//		tv2.setAverageYield(new String("works2"));
//		tv2.setAverageOdds(new String("works2"));
//		tv2.setNumberOfBets(new String("works2"));
//		tv2.setPercentWeGet(new String("works2"));
//		tv2.setPercentOver95(new String("works2"));
//		tv2.setAverageLiquidity(new String("works2"));
//		tv2.setPercentOfTipsFound(new String("works2"));
//		tv2.setFlatStakeYield(new String("works2"));
//		
//		List<StatsRow> arr = new ArrayList<TableValue>();
//		arr.add(tv);
//		arr.add(tv2);
		data = FXCollections.observableList(rows);
		
		tableKindOfBet.getItems().setAll(data);
		tableKindOfBet.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<StatsRow>() {

			@Override
			public void onChanged(ListChangeListener.Change<? extends StatsRow> c) {
//			     for(StatsRow t : c.getList()) {
//			    	 System.out.println(t);
//			     }
			     mainC.getTableLastBetsController().setDataList(c.getList());
			}
		});
		/**
		 * TEST TO FILL IN TABLES WITH DATA
		 */
	}
	
	/**
	 * Notify MainController with the current msg
	 */
	private void notifyMainController() {
		setChanged();
		notifyObservers(new ObservableMessage(OPTIONS_TABLEKINDOFBET_ID, null)); 
	}
	
	/**
	 * Select the last used RadioButton
	 * @param filters
	 */
	public void updateSettings(FilterSettingsContainer filters) {
//		this.msg = filters.getDateRangeMessage();
//		if (this.msg == null) return;
	}
	
	public ObservableList<StatsRow> getData() {
		return data;
	}
	
//	public static class CustomTableCell extends TableCell<StatsRow, Object>{
//
//	    private Label label;
//
//	    private DecimalFormat df ;
//	    private final NumberFormat nf = NumberFormat.getNumberInstance();
//
//        {
//             nf.setMaximumFractionDigits(2);
//             nf.setMinimumFractionDigits(2);
//        }
//        
//	    public CustomTableCell() {
//	        Locale locale  = new Locale("en", "UK");
//	        String pattern = "###,###.##";
//	        df = (DecimalFormat) NumberFormat.getNumberInstance(locale);
//	        df.applyPattern(pattern);
//	        createTextField();
//	    }
//
//	    @Override
//	    public void updateItem(Object item, boolean empty) {
//	        super.updateItem(item, empty);
//
//	        int columnNo = getTableView().getColumns().indexOf(getTableColumn());
//	        if (empty) {
//	            setText("null 1");
//	        } else {
//	        	if (item instanceof String) {
//                	setText((String) item);
//                	if (((String) item).isEmpty()) {
//                		setText("Empty String");
//                	}
//                } else if (item instanceof Date) {
//					setText(((Date) item).toString());
//	        	} else if (item instanceof Double) {
//	        		setText(((Double) item).toString());
//	        	}
//                if (columnNo == 6) {
////                	setTextFill(Color.WHITE);
//                	double netWon = 0;
//                	try {
//                		netWon = Double.parseDouble(getText());
//                		if (netWon < 0) {
//                			setStyle("-fx-background-color:#F64D54");		//RED
//                		} else if (netWon > 0){ 
//                			setStyle("-fx-background-color:#8CDD81");		//GREEN
//                		}
//                	} catch (NumberFormatException e) {
//                		System.out.println(e);
//                	} catch (NullPointerException e) {
//                		System.out.println(e);
//                		System.out.println("getText(): " + getText());
//                		System.out.println("netWon: " + netWon);
//                	}
//                	setText(nf.format(netWon));
//                }
//                setAlignment(Pos.CENTER);
//	        }
//	    }
//
//	    private String getString() {
//	        return getItem() == null ? "null 2" : (String) getItem();
//	    }
//
//	    private void createTextField(){
//	        label = new Label();
//	        label.setText( getString() );
//	        
//	        StringConverter<Object> converter = new StringConverter<Object>() {
//		        private final NumberFormat nf = NumberFormat.getNumberInstance();
//
//		        {
//		             nf.setMaximumFractionDigits(2);
//		             nf.setMinimumFractionDigits(2);
//		        }
//
//		        
//		        
//		        @Override public String fromString(final String s) {
//		            // Don't need this, unless table is editable, see DoubleStringConverter if needed
//		            return null; 
//		        }
//
//				@Override
//				public String toString(Object value) {
//					if (value instanceof String) {
//						return (String) value;
//					} else if (value instanceof Date) {
//							return ((Date) value).toString();
//		        	} else if (value instanceof Double) {
////		        		return ((Double) value).toString();
//		        		return nf.format(((Double) value));
//		        	}
//					return "NOT WORKING";
//				}
//	        };
//	        
//	        TextFormatter textFormatter = new TextFormatter<>(converter,  0.0, c ->
//	        {
//	            if (c.getControlNewText().isEmpty()) {
//	                return c;
//	            }
//	            ParsePosition parsePosition = new ParsePosition( 0 );
//	            Object object = df.parse( c.getControlNewText(), parsePosition );
//
//	            if ( object == null || parsePosition.getIndex() < c.getControlNewText().length() )
//	            {
//	                return null;
//	            }
//	            else
//	            {
//	                return c;
//	            }
//	        } ) ;
//
//	        // add filter to allow for typing only integer
//	        label.setTextFormatter( textFormatter);
////	        textField.setMinWidth( this.getWidth() - this.getGraphicTextGap() * 2 );
//
//	        // commit on Enter
////	        textFormatter.valueProperty().addListener((obs, oldValue, newValue) -> {
////	            commitEdit(newValue);
////	        });
//	    }
//	}
	
}

//newTC.setCellFactory(TextFieldTableCell.<StatsRow, Object>forTableColumn(new StringConverter<Object>() {
//    private final NumberFormat nf = NumberFormat.getNumberInstance();
//
//    {
//         nf.setMaximumFractionDigits(2);
//         nf.setMinimumFractionDigits(2);
//    }
//
//
//    @Override public String fromString(final String s) {
//        // Don't need this, unless table is editable, see DoubleStringConverter if needed
//        return null; 
//    }
//
//	@Override
//	public String toString(Object value) {
//		try {
//			if (value instanceof String) {
//				return nf.format(Double.parseDouble((String) value));
//			} else {
//				return nf.format((Double) value);
//			}
//    	} catch (NumberFormatException n) {
//    		return (String)value;
//    	}
//	}
//}));
