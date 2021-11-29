package main.model.repositories;
import main.model.Post;
import main.model.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

//Page<Post> findAll(Pageable pageable);

List<Post> findByTextContaining(String query, Pageable pageable);

//@Query("SELECT main.model.Post FROM dfschema.posts WHERE main.model.Post.time = date")
List<Post> findByTimeEquals(String date, Pageable pageable);

List<Post> findByTagsContaining(Tag tag, Pageable pageable);

List<Post> findByTimeContaining(String year);
}
