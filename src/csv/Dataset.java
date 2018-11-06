package csv;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;


/**
 * @author makerspacestaff
 *
 * Represents a CSV Dataset to be plugged into the application.
 * Loaded using the Univocity Parser
 *
 * @param <T> Represents the Java Bean to be loaded with the data
 */
public class Dataset<T> {

	private String filePath;
	private Class<T> bean;
	private List<T> beans;
	private int maxColumns;
	
	public Dataset(String filePath, Class<T> bean, int maxColumns) {
		this.filePath = filePath;
		this.bean = bean;
		this.maxColumns = maxColumns;
	}
	
	private void loadCSVFile() {
		BeanListProcessor<T> rowProcessor = new BeanListProcessor<T>(this.bean);

		CsvParserSettings parserSettings = new CsvParserSettings();
		parserSettings.getFormat().setLineSeparator("\n");
		parserSettings.setRowProcessor(rowProcessor);
		parserSettings.setHeaderExtractionEnabled(true);
		parserSettings.setMaxColumns(this.maxColumns);
		parserSettings.setIgnoreLeadingWhitespaces(true);
		parserSettings.setIgnoreTrailingWhitespaces(true);

		CsvParser parser = new CsvParser(parserSettings);
		try {
			parser.parse(new FileReader(this.filePath));
			this.beans = rowProcessor.getBeans();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public Class<T> getBean() {
		return this.bean;
	}

	public void setBean(Class<T> bean) {
		this.bean = bean;
	}

	public List<T> getBeans() {
		if (this.beans == null)
			this.loadCSVFile();
		return beans;
	}

	public void setBeans(List<T> beans) {
		this.beans = beans;
	}

	public void remove(T instance) {
		this.beans.remove(instance);
	}
}
