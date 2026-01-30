package com.cooking.controller.admin;

import com.cooking.common.Result;
import com.cooking.entity.*;
import com.cooking.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 数据初始化控制器
 */
@RestController
@RequestMapping("/api/admin/data-init")
public class DataInitController {

    @Autowired
    private BannerService bannerService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DishService dishService;

    @Autowired
    private DishIngredientService dishIngredientService;

    @Autowired
    private DishStepService dishStepService;

    @Autowired
    private UserFavoriteService userFavoriteService;

    /**
     * 初始化Banner数据
     */
    @PostMapping("/banner")
    public Result<String> initBanner() {
        // 清空现有数据
        bannerService.remove(null);

        // 创建Banner数据 - 使用默认图片占位符
        List<Banner> banners = Arrays.asList(
            createBanner("经典红烧肉", "default-banner.jpg", 1, "1", 1),
            createBanner("香辣宫保鸡丁", "default-banner.jpg", 1, "2", 2),
            createBanner("清爽凉拌黄瓜", "default-banner.jpg", 1, "3", 3),
            createBanner("营养蒸蛋羹", "default-banner.jpg", 1, "4", 4)
        );

        bannerService.saveBatch(banners);
        return Result.success("Banner数据初始化成功");
    }

    /**
     * 初始化分类数据
     */
    @PostMapping("/category")
    public Result<String> initCategory() {
        // 清空现有数据
        categoryService.remove(null);

        // 创建分类数据 - 使用默认图片占位符
        List<Category> categories = Arrays.asList(
            createCategory("家常菜", "default-category.jpg", 1),
            createCategory("川菜", "default-category.jpg", 2),
            createCategory("粤菜", "default-category.jpg", 3),
            createCategory("湘菜", "default-category.jpg", 4),
            createCategory("素食", "default-category.jpg", 5),
            createCategory("汤品", "default-category.jpg", 6),
            createCategory("甜品", "default-category.jpg", 7),
            createCategory("小食", "default-category.jpg", 8)
        );

        categoryService.saveBatch(categories);
        return Result.success("分类数据初始化成功");
    }

    /**
     * 初始化菜品数据
     */
    @PostMapping("/dish")
    public Result<String> initDish() {
        // 清空现有数据
        dishService.remove(null);

        // 创建菜品数据 - 使用在线示例图片
        List<Dish> dishes = Arrays.asList(
            // 家常菜
            createDish(1L, "红烧肉", "https://images.unsplash.com/photo-1546833999-b9f581a1996d?w=400&h=300&fit=crop", "肥而不腻，入口即化的经典红烧肉", 2, 60, 4, 450, 1250, 89, 15),
            createDish(1L, "糖醋排骨", "https://images.unsplash.com/photo-1565299624946-b28f40a0ca4b?w=400&h=300&fit=crop", "酸甜可口，老少皆宜的经典菜品", 2, 45, 3, 380, 980, 76, 12),
            createDish(1L, "麻婆豆腐", "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=400&h=300&fit=crop", "麻辣鲜香，下饭神器", 1, 20, 2, 220, 756, 45, 8),
            createDish(1L, "蒸蛋羹", "https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=400&h=300&fit=crop", "嫩滑如丝，营养丰富", 1, 15, 2, 120, 432, 23, 5),
            
            // 川菜
            createDish(2L, "宫保鸡丁", "https://images.unsplash.com/photo-1540420773420-3366772f4999?w=400&h=300&fit=crop", "川菜经典，香辣下饭", 2, 25, 3, 320, 1120, 67, 11),
            createDish(2L, "水煮鱼", "https://images.unsplash.com/photo-1547592180-85f173990554?w=400&h=300&fit=crop", "麻辣鲜香，鱼肉嫩滑", 3, 40, 4, 380, 890, 54, 9),
            createDish(2L, "回锅肉", "https://images.unsplash.com/photo-1551024506-0bccd828d307?w=400&h=300&fit=crop", "川菜之王，香辣可口", 2, 30, 3, 420, 678, 43, 7),
            
            // 粤菜
            createDish(3L, "白切鸡", "https://images.unsplash.com/photo-1555939594-58d7cb561ad1?w=400&h=300&fit=crop", "清淡鲜美，原汁原味", 1, 35, 4, 280, 567, 34, 6),
            createDish(3L, "蒸排骨", "https://images.unsplash.com/photo-1565958011703-44f9829ba187?w=400&h=300&fit=crop", "清蒸原味，营养健康", 1, 30, 3, 250, 445, 28, 4),
            
            // 湘菜
            createDish(4L, "剁椒鱼头", "https://images.unsplash.com/photo-1563379091339-03246963d51a?w=400&h=300&fit=crop", "香辣开胃，湘菜代表", 2, 35, 4, 300, 389, 25, 3),
            createDish(4L, "口水鸡", "https://images.unsplash.com/photo-1598515214211-89d3c73ae83b?w=400&h=300&fit=crop", "麻辣鲜香，口感丰富", 2, 25, 3, 280, 334, 21, 2),
            
            // 素食
            createDish(5L, "凉拌黄瓜", "https://images.unsplash.com/photo-1540420773420-3366772f4999?w=400&h=300&fit=crop", "清爽解腻，简单易做", 1, 10, 2, 50, 289, 18, 1),
            createDish(5L, "蒜蓉菠菜", "https://images.unsplash.com/photo-1576045057995-568f588f82fb?w=400&h=300&fit=crop", "营养丰富，清香可口", 1, 8, 2, 80, 234, 15, 1),
            
            // 汤品
            createDish(6L, "冬瓜排骨汤", "https://images.unsplash.com/photo-1547592180-85f173990554?w=400&h=300&fit=crop", "清热解暑，营养滋补", 1, 90, 4, 180, 198, 12, 1),
            createDish(6L, "番茄鸡蛋汤", "https://images.unsplash.com/photo-1571091718767-18b5b1457add?w=400&h=300&fit=crop", "酸甜开胃，家常美味", 1, 15, 2, 120, 167, 10, 1),
            
            // 甜品
            createDish(7L, "红豆沙", "https://images.unsplash.com/photo-1551024506-0bccd828d307?w=400&h=300&fit=crop", "香甜可口，传统甜品", 1, 60, 4, 200, 145, 9, 1),
            createDish(7L, "银耳莲子汤", "https://images.unsplash.com/photo-1578662996442-48f60103fc96?w=400&h=300&fit=crop", "滋阴润燥，美容养颜", 1, 45, 3, 150, 123, 8, 1),
            
            // 小食
            createDish(8L, "煎饺", "https://images.unsplash.com/photo-1496116218417-1a781b1c416c?w=400&h=300&fit=crop", "外酥内嫩，香味扑鼻", 2, 20, 2, 280, 98, 6, 1),
            createDish(8L, "小笼包", "https://images.unsplash.com/photo-1563245372-f21724e3856d?w=400&h=300&fit=crop", "皮薄馅大，汤汁丰富", 3, 40, 3, 320, 87, 5, 1),
            createDish(8L, "春卷", "https://images.unsplash.com/photo-1559847844-d721426d6edc?w=400&h=300&fit=crop", "酥脆可口，营养丰富", 2, 25, 3, 250, 76, 4, 1)
        );

        dishService.saveBatch(dishes);
        return Result.success("菜品数据初始化成功");
    }

    /**
     * 初始化所有数据
     */
    @PostMapping("/all")
    public Result<String> initAll() {
        initBanner();
        initCategory();
        initDish();
        initDishIngredients();
        initDishSteps();
        return Result.success("所有数据初始化成功");
    }

    /**
     * 初始化菜品食材数据
     */
    @PostMapping("/dish-ingredients")
    public Result<String> initDishIngredients() {
        // 清空现有数据
        dishIngredientService.remove(null);

        // 为红烧肉添加食材
        List<DishIngredient> ingredients = Arrays.asList(
            createIngredient(1L, "五花肉", "500", "克"),
            createIngredient(1L, "生抽", "2", "勺"),
            createIngredient(1L, "老抽", "1", "勺"),
            createIngredient(1L, "冰糖", "30", "克"),
            createIngredient(1L, "料酒", "2", "勺"),
            createIngredient(1L, "葱", "2", "根"),
            createIngredient(1L, "姜", "3", "片"),
            createIngredient(1L, "八角", "2", "个"),
            
            // 为糖醋排骨添加食材
            createIngredient(2L, "排骨", "500", "克"),
            createIngredient(2L, "白糖", "3", "勺"),
            createIngredient(2L, "醋", "2", "勺"),
            createIngredient(2L, "生抽", "1", "勺"),
            createIngredient(2L, "料酒", "1", "勺"),
            createIngredient(2L, "盐", "适量", ""),
            createIngredient(2L, "葱花", "适量", ""),
            createIngredient(2L, "芝麻", "适量", ""),
            
            // 为麻婆豆腐添加食材
            createIngredient(3L, "嫩豆腐", "1", "块"),
            createIngredient(3L, "肉末", "100", "克"),
            createIngredient(3L, "豆瓣酱", "1", "勺"),
            createIngredient(3L, "花椒粉", "适量", ""),
            createIngredient(3L, "葱花", "适量", ""),
            createIngredient(3L, "蒜末", "适量", ""),
            createIngredient(3L, "生抽", "1", "勺"),
            createIngredient(3L, "淀粉", "1", "勺"),
            
            // 为蒸蛋羹添加食材
            createIngredient(4L, "鸡蛋", "3", "个"),
            createIngredient(4L, "温水", "150", "毫升"),
            createIngredient(4L, "盐", "少许", ""),
            createIngredient(4L, "香油", "几滴", ""),
            createIngredient(4L, "葱花", "适量", ""),
            
            // 为宫保鸡丁添加食材
            createIngredient(5L, "鸡胸肉", "300", "克"),
            createIngredient(5L, "花生米", "50", "克"),
            createIngredient(5L, "干辣椒", "10", "个"),
            createIngredient(5L, "花椒", "1", "勺"),
            createIngredient(5L, "葱", "2", "根"),
            createIngredient(5L, "姜", "1", "块"),
            createIngredient(5L, "蒜", "3", "瓣"),
            createIngredient(5L, "生抽", "2", "勺"),
            createIngredient(5L, "料酒", "1", "勺"),
            createIngredient(5L, "白糖", "1", "勺"),
            createIngredient(5L, "醋", "1", "勺"),
            createIngredient(5L, "淀粉", "1", "勺")
        );

        dishIngredientService.saveBatch(ingredients);
        return Result.success("菜品食材数据初始化成功");
    }

    /**
     * 初始化菜品制作步骤数据
     */
    @PostMapping("/dish-steps")
    public Result<String> initDishSteps() {
        // 清空现有数据
        dishStepService.remove(null);

        // 为红烧肉添加制作步骤
        List<DishStep> steps = Arrays.asList(
            createStep(1L, 1, "五花肉洗净切块，冷水下锅焯水去腥", null),
            createStep(1L, 2, "热锅下油，放入冰糖炒糖色至焦糖色", null),
            createStep(1L, 3, "下入焯好水的五花肉翻炒上色", null),
            createStep(1L, 4, "加入葱姜八角，倒入生抽老抽料酒", null),
            createStep(1L, 5, "加入开水没过肉块，大火烧开转小火炖40分钟", null),
            createStep(1L, 6, "大火收汁，撒葱花即可出锅", null),
            
            // 为糖醋排骨添加制作步骤
            createStep(2L, 1, "排骨洗净切段，冷水下锅焯水", null),
            createStep(2L, 2, "热锅下油，放入排骨煎至两面金黄", null),
            createStep(2L, 3, "调制糖醋汁：白糖3勺、醋2勺、生抽1勺", null),
            createStep(2L, 4, "倒入糖醋汁，加少量水焖煮15分钟", null),
            createStep(2L, 5, "大火收汁，撒葱花和芝麻即可", null),
            
            // 为麻婆豆腐添加制作步骤
            createStep(3L, 1, "豆腐切块，用盐水浸泡10分钟", null),
            createStep(3L, 2, "热锅下油，爆香蒜末和豆瓣酱", null),
            createStep(3L, 3, "下入肉末炒散，加生抽调色", null),
            createStep(3L, 4, "加水烧开，下入豆腐块轻轻推散", null),
            createStep(3L, 5, "用淀粉水勾芡，撒花椒粉和葱花即可", null),
            
            // 为蒸蛋羹添加制作步骤
            createStep(4L, 1, "鸡蛋打散，加入温水和盐搅拌均匀", null),
            createStep(4L, 2, "过筛去除泡沫，倒入蒸碗中", null),
            createStep(4L, 3, "盖上保鲜膜，用牙签扎几个小孔", null),
            createStep(4L, 4, "水开后蒸10-12分钟至凝固", null),
            createStep(4L, 5, "出锅后滴几滴香油，撒葱花即可", null),
            
            // 为宫保鸡丁添加制作步骤
            createStep(5L, 1, "鸡胸肉切丁，用料酒和淀粉腌制15分钟", null),
            createStep(5L, 2, "花生米过油炸至酥脆盛起", null),
            createStep(5L, 3, "热锅下油，爆香干辣椒和花椒", null),
            createStep(5L, 4, "下入鸡丁炒至变色，加葱姜蒜爆香", null),
            createStep(5L, 5, "调入生抽、糖、醋炒匀，最后加入花生米即可", null)
        );

        dishStepService.saveBatch(steps);
        return Result.success("菜品制作步骤数据初始化成功");
    }

    /**
     * 迁移收藏数据，为现有收藏记录添加status字段
     */
    @PostMapping("/migrate-favorites")
    public Result<String> migrateFavorites() {
        try {
            // 获取所有收藏记录
            List<UserFavorite> favorites = userFavoriteService.list();
            
            // 为没有status字段的记录设置默认值
            int updatedCount = 0;
            for (UserFavorite favorite : favorites) {
                if (favorite.getStatus() == null) {
                    favorite.setStatus(1); // 设置为已收藏状态
                    userFavoriteService.updateById(favorite);
                    updatedCount++;
                }
            }
            
            return Result.success("收藏数据迁移成功，更新了 " + updatedCount + " 条记录");
        } catch (Exception e) {
            return Result.error("收藏数据迁移失败：" + e.getMessage());
        }
    }

    private Banner createBanner(String title, String image, Integer linkType, String linkValue, Integer sort) {
        Banner banner = new Banner();
        banner.setTitle(title);
        banner.setImage(image);
        banner.setLinkType(linkType);
        banner.setLinkValue(linkValue);
        banner.setSort(sort);
        banner.setStatus(1);
        return banner;
    }

    private Category createCategory(String name, String icon, Integer sort) {
        Category category = new Category();
        category.setName(name);
        category.setIcon(icon);
        category.setSort(sort);
        category.setStatus(1);
        return category;
    }

    private Dish createDish(Long categoryId, String name, String image, String description, 
                           Integer difficulty, Integer cookingTime, Integer servings, Integer calories,
                           Integer viewCount, Integer collectCount, Integer shareCount) {
        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        dish.setName(name);
        dish.setImage(image);
        dish.setDescription(description);
        dish.setDifficulty(difficulty);
        dish.setCookingTime(cookingTime);
        dish.setServings(servings);
        dish.setCalories(calories);
        dish.setViewCount(viewCount);
        dish.setCollectCount(collectCount);
        dish.setShareCount(shareCount);
        dish.setStatus(1);
        return dish;
    }

    private DishIngredient createIngredient(Long dishId, String name, String amount, String unit) {
        DishIngredient ingredient = new DishIngredient();
        ingredient.setDishId(dishId);
        ingredient.setName(name);
        ingredient.setAmount(amount);
        ingredient.setUnit(unit);
        return ingredient;
    }

    private DishStep createStep(Long dishId, Integer stepNumber, String description, String image) {
        DishStep step = new DishStep();
        step.setDishId(dishId);
        step.setStepNumber(stepNumber);
        step.setDescription(description);
        step.setImage(image);
        return step;
    }
}