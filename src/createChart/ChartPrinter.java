package createChart;

import javafx.stage.Stage;

public final class ChartPrinter {
    public static void print(Stage stage, double[] dataToShow, double precision, String tittle){
        Chart chart = new Chart();
        chart.drawLineChart(stage, dataToShow, tittle, precision);
    }
}
