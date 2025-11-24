"""
Simple in-memory caching for the agent service.
"""

import hashlib
import json
import time
from typing import Any, Dict, Optional
from threading import Lock


class CacheEntry:
    """Represents a cached entry with TTL."""

    def __init__(self, value: Any, ttl_seconds: int):
        self.value = value
        self.expires_at = time.time() + ttl_seconds
        self.created_at = time.time()

    def is_expired(self) -> bool:
        """Check if the cache entry has expired."""
        return time.time() > self.expires_at

    def get_age_seconds(self) -> float:
        """Get the age of the cache entry in seconds."""
        return time.time() - self.created_at


class SimpleCache:
    """Simple in-memory cache with TTL support."""

    def __init__(self, max_size: int = 1000, default_ttl_seconds: int = 300):
        self.max_size = max_size
        self.default_ttl_seconds = default_ttl_seconds
        self.cache: Dict[str, CacheEntry] = {}
        self.lock = Lock()
        self.hits = 0
        self.misses = 0

    def _generate_key(self, key_components: list) -> str:
        """Generate a cache key from components."""
        # Sort the components to ensure consistent key generation
        sorted_components = sorted(key_components)
        key_string = json.dumps(sorted_components, sort_keys=True)
        return hashlib.md5(key_string.encode()).hexdigest()

    def get(self, key_components: list) -> Optional[Any]:
        """Get a value from cache."""
        key = self._generate_key(key_components)

        with self.lock:
            entry = self.cache.get(key)
            if entry and not entry.is_expired():
                self.hits += 1
                return entry.value
            elif entry and entry.is_expired():
                # Remove expired entry
                del self.cache[key]

        self.misses += 1
        return None

    def set(self, key_components: list, value: Any, ttl_seconds: Optional[int] = None) -> None:
        """Set a value in cache."""
        key = self._generate_key(key_components)
        ttl = ttl_seconds or self.default_ttl_seconds

        with self.lock:
            # If cache is full, remove oldest entries
            if len(self.cache) >= self.max_size:
                # Remove expired entries first
                expired_keys = [k for k, v in self.cache.items() if v.is_expired()]
                for k in expired_keys:
                    del self.cache[k]

                # If still full, remove oldest entries
                if len(self.cache) >= self.max_size:
                    oldest_keys = sorted(
                        self.cache.keys(),
                        key=lambda k: self.cache[k].created_at
                    )[:10]  # Remove 10 oldest
                    for k in oldest_keys:
                        del self.cache[k]

            self.cache[key] = CacheEntry(value, ttl)

    def clear(self) -> None:
        """Clear all cache entries."""
        with self.lock:
            self.cache.clear()
            self.hits = 0
            self.misses = 0

    def get_stats(self) -> Dict[str, Any]:
        """Get cache statistics."""
        with self.lock:
            total_requests = self.hits + self.misses
            hit_rate = (self.hits / total_requests) if total_requests > 0 else 0.0

            return {
                "size": len(self.cache),
                "max_size": self.max_size,
                "hits": self.hits,
                "misses": self.misses,
                "hit_rate": round(hit_rate, 3),
                "total_requests": total_requests,
            }

    def cleanup_expired(self) -> int:
        """Remove expired entries and return count of removed entries."""
        with self.lock:
            expired_keys = [k for k, v in self.cache.items() if v.is_expired()]
            for k in expired_keys:
                del self.cache[k]
            return len(expired_keys)