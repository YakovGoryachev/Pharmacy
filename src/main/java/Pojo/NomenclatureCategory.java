package Pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.List;

@Entity
public class NomenclatureCategory {
    public NomenclatureCategory(){
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String code;
    private Long parentId;
    private boolean isSystem;
    @CreationTimestamp
    private Instant createdAt;

    @ManyToMany(mappedBy = "nomenclatureCategories", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("nomenclatureCategories")
    private List<Nomenclature> nomenclatures;

    public List<Nomenclature> getNomenclatures() {
        return nomenclatures;
    }

    public void setNomenclatures(List<Nomenclature> nomenclatures) {
        this.nomenclatures = nomenclatures;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public boolean isSystem() {
        return isSystem;
    }

    public void setSystem(boolean system) {
        isSystem = system;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
