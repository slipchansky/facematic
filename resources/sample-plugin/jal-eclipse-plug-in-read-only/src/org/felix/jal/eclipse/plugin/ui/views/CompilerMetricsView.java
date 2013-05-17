package org.felix.jal.eclipse.plugin.ui.views;


import java.text.NumberFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;
import org.felix.jal.compiler.JalCompileResult;
import org.felix.jal.eclipse.plugin.builder.JalBuilder;

public class CompilerMetricsView extends ViewPart {
	private Canvas paintCanvas = null;
	private GC gc;
	private Display display;
	public static final String COMPILERMETRICS_VIEW_ID = "org.felix.jal.metrics.compilermetrics";
	private JalCompileResult lastCompileResult = null;
	private String commandExecutionResult = null;

	public CompilerMetricsView() {
		super();
	}
		
	public void refresh() {
		display.syncExec(new Runnable() {
			
			@Override
			public void run() {
				commandExecutionResult = null;
				paintCanvas.redraw();
			}
		});
	}
	
	public void refreshCommandExecution(final String result) {
		display.syncExec(new Runnable() {
			
			@Override
			public void run() {
				commandExecutionResult = result;
				paintCanvas.redraw();
			}
		});
	}
	
	
	@Override
	public void createPartControl(Composite parent) {
		display = parent.getDisplay();

		paintCanvas = new Canvas(parent,  SWT.BORDER | 
	            SWT.NO_MERGE_PAINTS | SWT.NONE); 

		paintCanvas.addPaintListener(new PaintListener() {			
			@Override
			public void paintControl(PaintEvent e) {
				gc = e.gc;
				drawStats();
			}
		});		
	}
	
	private void drawStats() {
		
		this.lastCompileResult = JalBuilder.lastCompileResult;
		gc.setForeground(display.getSystemColor(SWT.COLOR_BLUE));
		gc.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		gc.setLineStyle(SWT.LINE_SOLID);

		Rectangle clientArea = display.getClientArea();
		gc.fillRectangle(clientArea);
		
		int y = 5;

		Font titleFont = new Font(display, "Courier New", 10, SWT.BOLD); 
		
		if (commandExecutionResult != null) {
			y = drawText("Command Results", 5, y, titleFont, SWT.COLOR_BLACK);
			Font normalText = new Font(display, "Courier New", 8, SWT.NORMAL);
			drawText(commandExecutionResult, 5, y, normalText, SWT.COLOR_RED);
			return;
		}
		
		if (this.lastCompileResult != null) {
			y = drawText("Compiler Results", 5, y, titleFont, SWT.COLOR_BLACK);
			drawResults(y);
		}	
	}
	
	private void drawResults(int y) {
		Font normalText = new Font(display, "Courier New", 8, SWT.NORMAL);
		
		NumberFormat numFormat = NumberFormat.getNumberInstance();
		numFormat.setMaximumFractionDigits(2);
		
		JalCompileResult res = this.lastCompileResult;
		y = drawText(res.getCompiler(), 5, y, normalText, SWT.COLOR_BLACK);

		y += 5;
		
		Double value = res.getCodeAreaBytesUsed()*1.0/res.getCodeAreaBytesTotal()*100;
		drawProgressBar(10, y, 150, 10, 
				value.intValue(), 
				SWT.COLOR_BLACK, SWT.COLOR_RED);
		StringBuilder sb = new StringBuilder();
		sb.append(res.getCodeAreaBytesUsed());
		sb.append(" program bytes used from a possible ");
		sb.append(res.getCodeAreaBytesTotal());
		sb.append(" (");
		if (res.getNumErrors()==0)
			sb.append(numFormat.format(value));
		else
			sb.append("?");
		sb.append("%)");
		
		y = drawText(sb.toString(), 175, y-2, normalText, SWT.COLOR_BLACK);
		
		y += 5;
		
		value = res.getDataAreaBytesUsed()*1.0/res.getDataAreaBytesTotal()*100;
		drawProgressBar(10, y, 150, 10, 
				value.intValue(),
				SWT.COLOR_BLACK, SWT.COLOR_BLUE);

		sb = new StringBuilder();
		sb.append(res.getDataAreaBytesUsed());
		sb.append(" variable bytes used from a possible ");
		sb.append(res.getDataAreaBytesTotal());
		sb.append(" (");
		if (res.getNumErrors()==0)
			sb.append(numFormat.format(value));
		else
			sb.append("?");
		sb.append("%)");

		y = drawText(sb.toString(), 175, y-2, normalText, SWT.COLOR_BLACK);
		
		y += 5;
		
		value = res.getSoftStackAvailable()*1.0/2000*100;
		drawProgressBar(10, y, 150, 10, 
				value.intValue(),
				SWT.COLOR_BLACK, SWT.COLOR_GREEN);

		sb = new StringBuilder();
		sb.append(res.getSoftStackAvailable());
		sb.append(" software stack available");
		sb.append(" (");
		if (res.getNumErrors()==0)
			sb.append(numFormat.format(value));
		else
			sb.append("?");
		sb.append("%)");

		y = drawText(sb.toString(), 175, y-2, normalText, SWT.COLOR_BLACK);
		
		y += 5;		

		value = res.getHardStackDepthUsed()*1.0/res.getHardStackDepthTotal()*100;
		drawProgressBar(10, y, 150, 10, 
				value.intValue(),
				SWT.COLOR_BLACK, SWT.COLOR_DARK_MAGENTA);

		sb = new StringBuilder();
		sb.append(res.getHardStackDepthUsed());
		sb.append(" hardware stack used from a possible ");
		sb.append(res.getHardStackDepthTotal());
		sb.append(" (");
		if (res.getNumErrors()==0)
			sb.append(numFormat.format(value));
		else
			sb.append("?");
		sb.append("%)");

		y = drawText(sb.toString(), 175, y-2, normalText, SWT.COLOR_BLACK);
		
		y += 5;		

		sb = new StringBuilder();
		sb.append(res.getNumErrors());
		sb.append(" errors");
		if (res.getNumErrors()>0)
			drawText(sb.toString(), 10, y, normalText, SWT.COLOR_RED);
		else
			drawText(sb.toString(), 10, y, normalText, SWT.COLOR_BLACK);
		
		sb = new StringBuilder();
		sb.append(res.getNumWarnings());
		sb.append(" warnings");
		if (res.getNumWarnings()>0)
			y = drawText(sb.toString(), 100, y, normalText, SWT.COLOR_DARK_YELLOW);
		else
			y = drawText(sb.toString(), 100, y, normalText, SWT.COLOR_BLACK);

		if (res.getNumErrors()==0) {
			sb = new StringBuilder();
			sb.append(res.getTokens());
			sb.append(" tokens . ");
			sb.append(res.getChars());
			sb.append(" chars . ");
			sb.append(res.getLines());
			sb.append(" lines . ");
			sb.append(res.getFiles());
			sb.append(" files");
	
			Font tinyText = new Font(display, "Verdana", 6, SWT.BOLD | SWT.ITALIC);
			drawText(sb.toString(), 10, y, tinyText, SWT.COLOR_BLACK);
		}
		
	}
	
	private int drawText(String text, int x, int y, Font font, int foreColor) {
		gc.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		gc.setForeground(display.getSystemColor(foreColor));
		gc.setFont(font);
		gc.drawText(text, x, y);
		return y + gc.getFontMetrics().getHeight();
	}
	
	private int drawProgressBar(int x, int y, int width, int height, int fillperc, int colorBorder, int colorFill) {
		int fillwidth = new Double((width * fillperc*1.0) / 100).intValue();
		
		gc.setForeground(display.getSystemColor(colorBorder));
		gc.setLineWidth(1);
		gc.drawRoundRectangle(x, y, width, height, 5, 5);

		gc.setBackground(display.getSystemColor(colorFill));		
		gc.fillRoundRectangle (x+1, y+1, fillwidth-1, height-1, 5, 5);
		
		return y + 11;
	}
	

	@Override
	public void setFocus() {
		paintCanvas.setFocus();
	}

}
