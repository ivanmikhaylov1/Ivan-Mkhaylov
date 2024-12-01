-- Первый запрос
SELECT count(*) AS user_count
FROM profile
WHERE profile_id NOT IN (SELECT profile_id FROM post);
-- Второй запрос
SELECT post.post_id
FROM post
JOIN (
    SELECT post_id
    FROM comment
    GROUP BY post_id
    HAVING count(*) = 2
) comment ON post.post_id = comment.post_id
WHERE post.title ~ '^[0-9]' AND length(post.content) > 20
ORDER BY post.post_id;
-- Третий запрос
SELECT post.post_id
FROM post
LEFT JOIN comment ON post.post_id = comment.post_id
GROUP BY post.post_id
HAVING count(comment.comment_id) <= 1
ORDER BY post.post_id
LIMIT 10;
