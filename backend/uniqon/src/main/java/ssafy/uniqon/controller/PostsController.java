package ssafy.uniqon.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import ssafy.uniqon.global.response.Response;
import ssafy.uniqon.service.PostCreateService;
import ssafy.uniqon.service.PostReadService;
import ssafy.uniqon.service.PostUpdateService;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

import static ssafy.uniqon.global.response.Response.OK;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sales")
@Tag(name = "판매 API")
@CrossOrigin("*")
public class PostsController {

    public record RegisterPostWebRequest(
            Integer price,
            String content,
            String title,
            Integer nftId
    ) {
    }

    public record UpdatePostWebRequest(
            Integer price,
            String title,
            String content,
            String walletAddress
    ){}

    public record postListsWebResponse(
            Integer price,
            String title,
            String species,
            String nickname,
            String image,
            Boolean wishCheck
    )
    {}

    public record postDetailWebResponse(
            String profileImage,
            String nickname,
            String species,
            String name,
            String feature,
            Integer age,
            String image,
            Integer price,
            String content,
            Timestamp createDatetime,
            String title,
            Boolean wishCheck
    ){}
private final PostReadService postReadService;
private final PostCreateService postCreateService;
private final PostUpdateService postUpdateService;
    @Operation(summary="판매글 등록", description = "판매글을 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND")
    })
    @PostMapping("/register")
    public Response<?> registerPost(@RequestBody RegisterPostWebRequest req) {
        log.debug("# 판매글 등록시 데이터 : {}", req);
        postCreateService.createPost(req);
        return OK("success");
    }
    @Operation(summary="판매글 수정", description = "판매글을 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND")
    })
    @PutMapping("/update/{postId}")
    public Response<?> updatePost(@PathVariable Integer postId, @RequestBody UpdatePostWebRequest req){
        log.debug("# 판매글 수정 데이터 : {}",req);
        postUpdateService.updatePost(postId,req);
        return OK("success");
    }

    @DeleteMapping("/delete/{postId}")
    public Response<?> deletePost(@PathVariable int postId){
        return OK(null);
    }
    @Operation(summary="판매글 검색", description = "판매글을 검색합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND")
    })
    @GetMapping("/search/{word}")
    public Response<?> searchPost(@PathVariable String word, @RequestParam String walletAddress) {
        log.debug("# 검색어 word : {}", word);
        log.debug("# 사용자 walletAddress : {}", walletAddress);
        List<postListsWebResponse> postlist = postReadService.getSearchPostList(word,walletAddress);
        return OK(postlist);
    }

    @Operation(summary="판매글 상세 조회", description = "판매글 상세 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND")
    })
    @GetMapping("/detail/{postId}")
    public Response<?> getDetailPost(@PathVariable int postId, @RequestParam String walletAddress){
        log.debug("# 판매글 식별자 id : {}", postId);
        log.debug("# 사용자 walletAddress : {}", walletAddress);

        postDetailWebResponse post = postReadService.getPostDetail(postId, walletAddress);

        JSONObject obj = new JSONObject();
        obj.put("SellerInfo", Map.of("profileImage", post.profileImage, "nickname", post.nickname));
        obj.put("nftInfo", Map.of("species", post.species, "name", post.name, "feature", post.feature, "age", post.age, "image", post.image));
        obj.put("PostInfo", Map.of("price", post.price, "content", post.content, "createDatetime", post.createDatetime, "title", post.title, "wishCheck", post.wishCheck));

        return OK(obj);
    }

    @Operation(summary="판매글 조회", description = "판매글 리스트를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND")
    })
    @GetMapping("/post")
    public Response<?> getAllPostList(@RequestParam String walletAddress,@PageableDefault(size=10) Pageable pageable){
        log.debug("# 판매글 리스트 표시");
        List<postListsWebResponse> postlist = postReadService.getPostAll(walletAddress);
        return OK(postlist);
    }
}
