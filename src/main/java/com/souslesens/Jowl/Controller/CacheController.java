package com.souslesens.Jowl.Controller;

import com.souslesens.Jowl.model.GetClassAxiomsInput;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cache")
public class CacheController {

    private final CacheManager cacheManager;

    public CacheController(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    // Endpoint to delete all caches
    @DeleteMapping("/evictAll")
    public String evictAllCache() {
        cacheManager.getCache("ontologyCache").clear();
        return "All caches cleared.";
    }
    // Endpoint to delete cache for a specific graphName
    @DeleteMapping("")
    public String evictSingleCache(@RequestParam String graphName) {
        cacheManager.getCache("ontologyCache").evict(graphName);
        return "Cache for graphName '" + graphName + "' evicted.";
    }


}
