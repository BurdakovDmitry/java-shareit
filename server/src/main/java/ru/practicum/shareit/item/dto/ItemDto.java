package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private Long id;
        private String name;
        private String description;
        private Long requestId;
        private Boolean available;

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private List<CommentDto> comments;
}
