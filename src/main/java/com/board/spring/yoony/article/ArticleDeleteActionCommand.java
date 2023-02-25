package com.board.spring.yoony.article;

import com.board.spring.yoony.command.ActionCommand;
import com.board.spring.yoony.command.DependencyCommand;
import com.board.spring.yoony.error.CustomException;
import com.board.spring.yoony.error.ErrorCode;
import com.board.spring.yoony.file.FileDTO;
import com.board.spring.yoony.file.FileMapper;
import com.board.spring.yoony.util.RequestUtil;
import com.board.spring.yoony.util.Security;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

/**
 * 게시글 삭제 처리를 처리하는 커맨드
 *
 * @author yoony
 * @version 1.0
 * @see ActionCommand
 * @see DependencyCommand
 * @since 2023. 02. 26.
 */
@Service
@RequiredArgsConstructor
public class ArticleDeleteActionCommand implements ActionCommand {

  private Logger logger = LogManager.getLogger(ArticleDeleteActionCommand.class);

  private final DependencyCommand dependencyCommand;

  /**
   * 게시글 삭제 처리를 수행한다.
   *
   * @param request  HttpServletRequest
   * @param paramMap Map<String, Object> 처리에 필요한 파라미터를 담은 맵
   * @param model    Map<String, Object> 처리 결과를 담을 맵
   * @throws Exception
   * @throws CustomException
   * @version 1.0
   * @see ActionCommand#execute(HttpServletRequest, Map, Map)
   * @see ArticleMapper#selectArticle(long) 게시글 조회
   * @see ArticleMapper#selectPasswordCheck(ArticleDTO) 비밀번호 체크
   * @see ArticleMapper#deleteArticle(ArticleDTO) 게시글 삭제
   * @see FileMapper#deleteFile(FileDTO) 첨부파일 삭제
   * @since 2023. 02. 26.
   */
  @Override
  public void execute(HttpServletRequest request, Map<String, Object> paramMap,
      Map<String, Object> model) throws Exception {
    logger.debug("execute()");

    ArticleMapper articleMapper = dependencyCommand.getSqlSessionTemplate()
        .getMapper(ArticleMapper.class);
    long articleId = RequestUtil.getLongParameter(request.getParameter("articleId"));
    String password = Security.sha256Encrypt(request.getParameter("password"));

    if (articleId < 1) {
      throw new CustomException("게시물 ID가 유효하지 않습니다.", ErrorCode.ARTICLE_ID_NOT_VALID);
    }
    ArticleDTO targetArticleDTO = articleMapper.selectArticle(articleId);
    if (targetArticleDTO == null) {
      throw new CustomException("게시물이 존재하지 않습니다.", ErrorCode.ARTICLE_NOT_FOUND);
    }

    ArticleDTO deleteArticleDTO = new ArticleDTO();
    deleteArticleDTO.setArticleId(articleId);
    deleteArticleDTO.setPassword(password);

    boolean isPasswordValid = articleMapper.selectPasswordCheck(deleteArticleDTO);
    if (!isPasswordValid) {
      throw new CustomException("비밀번호가 일치하지 않습니다.", ErrorCode.ARTICLE_PASSWORD_NOT_VALID);
    }
    int result = articleMapper.deleteArticle(deleteArticleDTO);

    if (result < 1) {
      throw new CustomException("게시물 삭제에 실패했습니다.", ErrorCode.ARTICLE_DELETE_FAIL);
    }
    FileMapper fileMapper = dependencyCommand.getSqlSessionTemplate()
        .getMapper(FileMapper.class);

    fileMapper.deleteAllFile(articleId);
  }
}
