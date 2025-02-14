package com.forum.project.domain.question.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ViewCount {
    private String key;
    private Long count;

    public void increment() {
        this.count ++;
    }

    public void decrement() {
        if (count <= 0) return;
        this.count --;
    }
}
