package createChart;

import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class Chart {
    double[] dataToShow = null;
    private double precision;
    private String tittle = "Example";

    public void setDataToShow(double[] dataToShow) {
        this.dataToShow = dataToShow;
    }

    public void setPrecision(double precision) {
        this.precision = precision;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public void drawLineChart(Stage stage, double[] dataToShow,String title, double precision) {
        setDataToShow(dataToShow);
        setPrecision(precision);
        setTittle(title);
        generateLineChart(stage);
    }

    private void generateLineChart(Stage stage) {
        System.out.println( "charts.Chart printing: " + tittle);

        stage.setTitle("Line charts.Chart");
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        final LineChart<Number,Number> lineChart = new LineChart<>(xAxis, yAxis);

        xAxis.setLabel("x");
        yAxis.setLabel("y");
        lineChart.setTitle(tittle);

        XYChart.Series series = new XYChart.Series();
        series.setName("data");

        if(dataToShow != null)
        {
            for (int i = 0; i < dataToShow.length; i++)
                series.getData().add(new XYChart.Data(i*precision, dataToShow[i]));
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Look, you have not datas");
            alert.setContentText("Ooops, there was an error!");

            alert.showAndWait();
        }

        Scene scene  = new Scene(lineChart,1200,800);
        lineChart.getData().add(series);

        stage.setScene(scene);
        stage.show();
    }
}
