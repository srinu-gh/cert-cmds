@Component
public class ComplaintsFileDownload extends FileDownload{

	private final Logger log = LoggerFactory.getLogger(ComplaintsFileDownload.class);
	final private String fileName="ComplainceBookReport";
	
	public ComplaintsFileDownload() {
        super();
        super.setHeaders(new String[]{"Title", "Author", "Publisher","Published Date", "Book Price"});
        super.setAttributes(new String[]{"title", "author", "publisher","publishedDate", "bookPrice"});
    }

	@Override
	protected String getFileName() {
		return fileName;
	}

}
