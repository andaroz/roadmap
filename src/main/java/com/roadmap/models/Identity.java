package com.roadmap.models;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Identity {
    @NonNull
    private String name;
    @NonNull
    private String lastName;
}
