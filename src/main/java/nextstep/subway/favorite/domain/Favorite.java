package nextstep.subway.favorite.domain;

import javax.persistence.*;

@Entity
public class Favorite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long source;

    @Column(nullable = false)
    private Long target;

    @Column(nullable = false)
    private Long memberId;

    protected Favorite() {
        // empty
    }

    public Favorite(final Long source, final Long target, final Long memberId) {
        this.source = source;
        this.target = target;
        this.memberId = memberId;
    }

    public Long getId() {
        return this.id;
    }

    public Long getSource() {
        return this.source;
    }

    public Long getTarget() {
        return this.target;
    }
}
