package ba.unsa.si.docflow.service.workflow;

import ba.unsa.si.docflow.dto.workflow.StatusHistoryResponse;
import ba.unsa.si.docflow.response.ApiResponse;

import java.util.List;

public interface StatusHistoryService {

    ApiResponse<List<StatusHistoryResponse>> getStatusHistory(Long documentId);
}
