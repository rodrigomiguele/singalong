package org.rmiguele.singalong.domain.youtube;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Thumbnails {

    private Thumbnail medium;

    public Thumbnail getMedium() {
        return medium;
    }

    public void setMedium(Thumbnail medium) {
        this.medium = medium;
    }
}
