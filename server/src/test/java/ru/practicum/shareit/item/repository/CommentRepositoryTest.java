package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class CommentRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    private Item item;
    private Item newItem;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setName("User");
        user.setEmail("user@mail.com");
        user = userRepository.save(user);

        item = new Item();
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(user);
        itemRepository.save(item);

        newItem = new Item();
        newItem.setName("newItem");
        newItem.setDescription("newDescription");
        newItem.setAvailable(false);
        newItem.setOwner(user);
        itemRepository.save(newItem);

        Comment comment = new Comment();
        comment.setText("Comment!");
        comment.setAuthorName("User");
        comment.setItem(item);
        commentRepository.save(comment);

        Comment newComment = new Comment();
        newComment.setText("newComment");
        newComment.setAuthorName("newUser");
        newComment.setItem(newItem);
        commentRepository.save(newComment);
    }

    @Test
    void findAllByItem() {
        List<Comment> comments = commentRepository.findAllByItem(item);

        assertEquals(1, comments.size());
        assertEquals("Comment!", comments.getFirst().getText());
        assertEquals(item.getId(), comments.getFirst().getItem().getId());
    }

    @Test
    void findAllByItemIn() {
        List<Comment> result = commentRepository.findAllByItemIn(List.of(item, newItem));

        assertEquals(2, result.size());
    }
}