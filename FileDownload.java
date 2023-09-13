public abstract class FileDownload {

	private final Logger log = LoggerFactory.getLogger(FileDownload.class);

	private String[] headers = {};

	private String[] attributes = {};

	private String title;

	private String generatordTag;

	private int columnsToSkipForGenTag;

	private static final String SHEET_NAME = "SHEET-01";

	public String[] getHeaders() {
		return headers;
	}

	public void setHeaders(String[] headers) {
		this.headers = headers;
	}

	public String[] getAttributes() {
		return attributes;
	}

	public void setAttributes(String[] attributes) {
		this.attributes = attributes;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getGeneratordTag() {
		return generatordTag;
	}

	public void setGeneratordTag(String generatordTag) {
		this.generatordTag = generatordTag;
	}

	public int getColumnsToSkipForGenTag() {
		return columnsToSkipForGenTag;
	}

	public void setColumnsToSkipForGenTag(int columnsToSkipForGenTag) {
		this.columnsToSkipForGenTag = columnsToSkipForGenTag;
	}

	protected abstract String getFileName();

	protected DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");

	protected DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

	protected DateMapper dateMapper = Mappers.getMapper(DateMapper.class);

	protected String getOrientation() {
		return "LANDSCAPE";
	}

	public HttpHeaders getHttpHeadersForFile() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Access-Control-Expose-Headers", "Content-Disposition");
		headers.setContentType(
				MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
		headers.setContentDispositionFormData(getFileName() + ".xlsx", getFileName() + ".xlsx");
		return headers;
	}

	public byte[] getFile(List<?> list) {
		return getXLS(list);
	}

	protected byte[] getXLS(List<?> list) {
		if (list.size() > 0) {
			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet;
			int rowNum = 0;
			sheet = workbook.createSheet(SHEET_NAME);

			if (generatordTag != null && !generatordTag.isBlank() && title != null && !title.isBlank()) {
				Row row = sheet.createRow(rowNum);
				XSSFCellStyle style = workbook.createCellStyle();
				XSSFFont font = workbook.createFont();
				font.setBold(true);
				font.setItalic(true);
				style.setFont(font);
				Cell cell = row.createCell(columnsToSkipForGenTag);
				cell.setCellValue(generatordTag);
				cell.setCellStyle(style);
				rowNum = rowNum + 1;
				sheet.createRow(rowNum++);
			}

			if (generatordTag != null && !generatordTag.isBlank() && title != null && !title.isBlank()) {
				Row row = sheet.createRow(rowNum);
				XSSFCellStyle style = workbook.createCellStyle();
				XSSFFont font = workbook.createFont();
				font.setBold(true);
				font.setItalic(false);
				style.setFont(font);
				Cell cell = row.createCell(columnsToSkipForGenTag / 2);
				cell.setCellValue(title);
				cell.setCellStyle(style);
				rowNum = rowNum + 1;
				sheet.createRow(rowNum++);
			}

			Row row = sheet.createRow(rowNum);
			XSSFCellStyle style = workbook.createCellStyle();
			XSSFFont font = workbook.createFont();
			font.setBold(true);
			font.setItalic(false);
			style.setFont(font);
			DataFormat format = workbook.createDataFormat();
			XSSFCellStyle numberStyle = workbook.createCellStyle();
			numberStyle.setAlignment(HorizontalAlignment.RIGHT);
			numberStyle.setDataFormat(format.getFormat("0"));
			CellStyle wrapstyle = workbook.createCellStyle();
			wrapstyle.setWrapText(true);

			Cell cell = row.createCell(0);
			int i = 1;
			for (String header : getHeaders()) {
				cell.setCellStyle(style);
				cell.setCellValue(header);
				cell = row.createCell(i++);
			}

			getXLSBody(list, workbook);

			sheet.createFreezePane(0, 1);
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			try {
				workbook.write(outputStream);
				workbook.close();
				outputStream.close();
			} catch (IOException e) {
				throw new FileDownloadException(e.getMessage());
			}
			return outputStream.toByteArray();
		} else {
			throw new FileDownloadException("No data to download");
		}
	}

	protected void getXLSBody(List<?> list, XSSFWorkbook workbook) {
		int rowNum = 1;
		if (generatordTag != null && !generatordTag.isBlank() && title != null && !title.isBlank())
			rowNum = 5;
		Row row = null;
		int j = 0;
		Cell cell = null;
		CellStyle wrapstyle = workbook.createCellStyle();
		wrapstyle.setWrapText(true);
		XSSFSheet sheet = workbook.getSheet(SHEET_NAME);

		for (Object obj : list) {
			row = sheet.createRow(rowNum++);
			j = 0;
			for (String attribute : getAttributes()) {
				try {
					cell = row.createCell(j++);
					cell.setCellStyle(wrapstyle);
					Object val = PropertyUtils.getNestedProperty(obj, attribute);
					if (val instanceof LocalDate) {
						cell.setCellValue(dateFormat.format((LocalDate) val));
					} else if (val instanceof Long) {
						cell.setCellValue((Long) val);
					} else if (val instanceof String) {
						cell.setCellValue(val.toString());
					} else if (val instanceof Integer) {
						cell.setCellValue(((Integer) val));
					} else if (val instanceof ZonedDateTime) {
						cell.setCellValue(dateTimeFormat.format((ZonedDateTime) val));
					} else if (val instanceof LocalDateTime) {
						cell.setCellValue(dateTimeFormat.format((LocalDateTime) val));
					} else if (val instanceof Instant) {
						cell.setCellValue(dateFormat.format(dateMapper.toLocalDateFromInstant((Instant) val)));
					} else if (val instanceof Enum) {
						cell.setCellValue(val.toString());
					} else if (val instanceof Boolean) {
						cell.setCellValue((Boolean) val);
					} else if (val instanceof Double) {
						cell.setCellValue((Double) val);
					}
				} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
					throw new FileDownloadException(e.getMessage());
				} catch (NestedNullException e) {
					log.debug("Exception : {}", e);
				}
			}
		}
		while (j > 0) {
			sheet.autoSizeColumn(j--);
		}
	}
}
