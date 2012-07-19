package org.tyrant.client;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import org.tyrant.shared.FactionDTO;
import org.tyrant.shared.FactionStateSnapshot;
import org.tyrant.shared.UserWarResult;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.view.client.ListDataProvider;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class FactionPortal implements EntryPoint {

	private final FactionSnapshotServiceAsync factionSnapshotService = GWT.create(FactionSnapshotService.class);

	private Panel intelPanel;

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		RootPanel rootPanel = RootPanel.get();

		final HTML resultsHtml = new HTML();

		final Button sendButton = new Button("Get intel");
		rootPanel.add(sendButton);
		rootPanel.add(new HTML("<br/>"));
		ClickHandler ch = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				sendButton.setEnabled(false);
				factionSnapshotService.greetServer("", new AsyncCallback<FactionDTO>() {
					public void onFailure(Throwable caught) {
					}

					public void onSuccess(FactionDTO result) {
						resultsHtml.setHTML(result.statsHtml);
						dataProvider.getList().clear();
						for (UserWarResult i : result.userResultsForLastWars) {
							dataProvider.getList().add(i);
						}

						sendButton.setEnabled(true);
					}
				});
			}
		};

		TabPanel tabPanel = new TabPanel();
		Panel p = new FlowPanel();

		Grid rankPanel = new Grid(1, 2);
		rankPanel.setWidth("2048");

		tabPanel.add(rankPanel, "Wars history");
		tabPanel.add(p, "?");

		rootPanel.add(tabPanel, 10, 40);
		tabPanel.setSize("686px", "825px");
		tabPanel.getTabBar().selectTab(0);
		sendButton.addClickHandler(ch);

		userResultsTable = createUserResultsTable();
		rankPanel.setWidget(0, 0, userResultsTable);
		rankPanel.setWidget(0, 1, resultsHtml);
		rankPanel.getCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);
		rankPanel.getCellFormatter().setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_TOP);

		intelPanel = new FlowPanel();
		p.add(intelPanel);
	}

	private CellTable<UserWarResult> createUserResultsTable() {
		CellTable<UserWarResult> table = new CellTable<UserWarResult>();
		TextColumn<UserWarResult> colName = new TextColumn<UserWarResult>() {
			@Override
			public String getValue(UserWarResult ur) {
				return ur.getUserName();
			}
		};
		TextColumn<UserWarResult> colBattles = new TextColumn<UserWarResult>() {
			@Override
			public String getValue(UserWarResult ur) {
				return ur.getBattlesFought().toString();
			}
		};
		TextColumn<UserWarResult> colLoyality = new TextColumn<UserWarResult>() {
			@Override
			public String getValue(UserWarResult ur) {
				return ur.getWins().toString();
			}
		};
		TextColumn<UserWarResult> colNetPts = new TextColumn<UserWarResult>() {
			@Override
			public String getValue(UserWarResult ur) {
				return String.valueOf(ur.getPoints() - ur.getPointsAgainst());
			}
		};

		colName.setSortable(true);
		colLoyality.setSortable(true);
		colBattles.setSortable(true);
		colNetPts.setSortable(true);

		table.addColumn(colName, "Name");
		table.addColumn(colLoyality, "Loyality");
		table.addColumn(colBattles, "Battles");
		table.addColumn(colNetPts, "Net Points");

		dataProvider = new ListDataProvider<UserWarResult>();
		dataProvider.addDataDisplay(table);
		ListHandler<UserWarResult> columnSortHandler = new ListHandler<UserWarResult>(dataProvider.getList());

		columnSortHandler.setComparator(colName, new Comparator<UserWarResult>() {
			@Override
			public int compare(UserWarResult o1, UserWarResult o2) {
				return o2.getUserName().compareTo(o1.getUserName());
			}
		});
		columnSortHandler.setComparator(colLoyality, new Comparator<UserWarResult>() {
			@Override
			public int compare(UserWarResult o1, UserWarResult o2) {
				return o2.getWins().compareTo(o1.getWins());
			}
		});
		columnSortHandler.setComparator(colBattles, new Comparator<UserWarResult>() {
			@Override
			public int compare(UserWarResult o1, UserWarResult o2) {
				return o2.getBattlesFought().compareTo(o1.getBattlesFought());
			}
		});
		columnSortHandler.setComparator(colNetPts, new Comparator<UserWarResult>() {
			@Override
			public int compare(UserWarResult o1, UserWarResult o2) {
				return new Integer(o2.getPoints() - o2.getPointsAgainst()).compareTo(new Integer(o1.getPoints() - o1.getPointsAgainst()));
			}
		});

		table.setPageSize(52);
		table.addColumnSortHandler(columnSortHandler);
		table.getColumnSortList().push(colNetPts);

		return table;
	}

	private String dateToString(Date dt) {
		return DateTimeFormat.getFormat("HH:mm").format(dt);
	}

	private String SPECIAL_STR = "$#$#$";

	private CellTable<UserWarResult> userResultsTable;

	private ListDataProvider<UserWarResult> dataProvider;

	public CellTable<?> fillIntelTable(Map<String, Map<Date, FactionStateSnapshot>> history) {
		CellTable<Map<Date, FactionStateSnapshot>> ctable = new CellTable<Map<Date, FactionStateSnapshot>>();

		final Iterator<String> it = history.keySet().iterator();
		ctable.addColumn(new TextColumn<Map<Date, FactionStateSnapshot>>() {
			@Override
			public String getValue(Map<Date, FactionStateSnapshot> m) {
				String i = it.next();
				return i.substring(0, i.indexOf(SPECIAL_STR));
			}
		}, "Faction");

		for (final Date dt : history.values().iterator().next().keySet()) {
			ctable.addColumn(new TextColumn<Map<Date, FactionStateSnapshot>>() {
				private Date date;
				private Date previous;

				@Override
				public String getValue(Map<Date, FactionStateSnapshot> m) {
					date = dt;
					if (m != null) {
						return get(m.get(date));
					} else {
						return "";
					}
				}

				private String getPreviousValue(Map<Date, FactionStateSnapshot> m) {
					if (m != null) {
						previous = date;
						for (Date d : m.keySet()) {
							if (date.equals(d)) {
								return get(m.get(previous));
							}
							previous = d;
						}
					}
					return null;
				}

				private String get(FactionStateSnapshot snapshot) {
					return snapshot == null ? null : "" + snapshot.rating;
				}

				@Override
				public void render(Context context, Map<Date, FactionStateSnapshot> m, SafeHtmlBuilder sb) {
					String val = getValue(m);
					String oldVal = getPreviousValue(m);
					boolean change = val != null && !val.equals(oldVal);
					boolean red = val != null && m != null && m.get(date) != null && m.get(date).defWarGoing;
					sb.appendHtmlConstant("<span " + (red ? "style=\"color: red\"" : "") + ">" + (change ? ("<b>" + val + "</b>") : val) + "</span>");
				}
			}, dateToString(dt));
		}
		ArrayList<Map<Date, FactionStateSnapshot>> list = new ArrayList<Map<Date, FactionStateSnapshot>>(history.values());
		ctable.setRowData(list);
		ListHandler<Map<Date, FactionStateSnapshot>> handler = new ListHandler<Map<Date, FactionStateSnapshot>>(list) {
		};
		ctable.addColumnSortHandler(handler);
		return ctable;
	}
}
