@RestController
@RequiredArgsConstructor
@RequestMapping("/api/report")
public class ComplainceReports {
	
    private final FileDownloadFactory fileDownloadFactory;

    private final ComplaintService complaintService;

    protected final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    
    @GetMapping("/books")
    public ResponseEntity<byte[]> downloadComplainceBookReport() {
    	FileDownload fd = fileDownloadFactory.getFileDownload("ComplainceBookReport");
        return ResponseEntity.ok().headers(fd.getHttpHeadersForFile()).
            body(complaintService.downloadTotalReceivedBooksReport(fd));
    }
    
    
}
