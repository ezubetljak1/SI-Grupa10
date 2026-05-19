package ba.unsa.si.docflow.service.dashboard;

import ba.unsa.si.docflow.dao.DocumentDAO;
import ba.unsa.si.docflow.dto.dashboard.DashboardResponse;
import ba.unsa.si.docflow.security.CurrentUser;
import ba.unsa.si.docflow.security.CurrentUserService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {
    private final DocumentDAO documentDAO;

    private final CurrentUserService currentUserService;

    @Override
    public DashboardResponse getCompanyDashboard() {

        CurrentUser currentUser =
                currentUserService.getCurrentUser();

        Long companyId = currentUser.companyId();

        DashboardResponse response = new DashboardResponse();

        response.setTotalDocuments(
                documentDAO.countByCompanyId(companyId)
        );

        response.setDocumentsByStatus(
                documentDAO.countDocumentsByStatus(companyId)
        );

        response.setDocumentsByResponsibleUser(
                documentDAO.countDocumentsByResponsibleUser(companyId)
        );

        return response;
    }
}
