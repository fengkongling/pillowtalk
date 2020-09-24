package com.flt.service.impl;

import com.flt.service.ProductGroupService;
import com.yofish.equity.product.biz.domain.Category;
import com.yofish.equity.product.biz.domain.Location;
import com.yofish.equity.product.biz.domain.ProductGroup;
import com.yofish.equity.product.biz.domain.ProductGroup4DestinationStrategy;
import com.yofish.equity.product.biz.domain.ProductGroup4ElectronicTicket;
import com.yofish.equity.product.biz.domain.ProductGroup4Hotel;
import com.yofish.equity.product.biz.domain.ProductGroup4Hotel4Facility;
import com.yofish.equity.product.biz.domain.ProductGroup4Hotel4Policy;
import com.yofish.equity.product.biz.domain.ProductGroup4Tag;
import com.yofish.equity.product.biz.domain.ProductGroupAttachment;
import com.yofish.equity.product.biz.domain.ProductGroupSupplier;
import com.yofish.equity.product.biz.domain.Supplier;
import com.yofish.equity.product.biz.domain.Tag;
import com.yofish.equity.product.biz.dto.DidaHotelAreaAttractionsDto;
import com.yofish.equity.product.biz.dto.DidaHotelDescriptionDto;
import com.yofish.equity.product.biz.dto.DidaHotelDto;
import com.yofish.equity.product.biz.dto.DidaHotelFacilityDto;
import com.yofish.equity.product.biz.dto.DidaHotelIdDto;
import com.yofish.equity.product.biz.dto.DidaHotelImageDto;
import com.yofish.equity.product.biz.dto.DidaHotelPolicyDto;
import com.yofish.equity.product.biz.dto.ProductGroup4ElectronicTicketDto;
import com.yofish.equity.product.biz.dto.ProductGroup4HotelDto;
import com.yofish.equity.product.biz.dto.ProductGroupAttachmentDto;
import com.yofish.equity.product.biz.dto.ProductGroupDto;
import com.yofish.equity.product.biz.repository.CategoryRepository;
import com.yofish.equity.product.biz.repository.DestinationStrategyRepository;
import com.yofish.equity.product.biz.repository.ProductGroup4DestinationStrategyRepository;
import com.yofish.equity.product.biz.repository.ProductGroup4Hotel4FacilityRepository;
import com.yofish.equity.product.biz.repository.ProductGroup4Hotel4PolicyRepository;
import com.yofish.equity.product.biz.repository.ProductGroup4HotelRepository;
import com.yofish.equity.product.biz.repository.ProductGroup4TagRepository;
import com.yofish.equity.product.biz.repository.ProductGroupAttachmentRepository;
import com.yofish.equity.product.biz.repository.ProductGroupRepository;
import com.yofish.equity.product.biz.repository.ProductGroupSupplierRepository;
import com.yofish.equity.product.biz.repository.SupplierRepository;
import com.yofish.equity.product.biz.repository.TagRepository;
import com.yofish.equity.product.biz.service.ProductGroup4Hotel4FacilityService;
import com.yofish.equity.product.biz.service.ProductGroupAttachmentService;
import com.yofish.equity.product.biz.service.ProductGroupSupplierService;
import com.yofish.equity.product.biz.service.ThirdPartyService;
import com.yofish.equity.product.biz.utils.PageDataHelper;
import com.yofish.equity.product.dto.ElectronicTicketReq4Filter;
import com.yofish.equity.product.dto.PageFilter;
import com.yofish.equity.product.dto.ProductGroup4Filter;
import com.yofish.equity.product.dto.ProductGroupAttachmentReq4Filter;
import com.yofish.equity.product.dto.ProductGroupReq4PublishStatus;
import com.yofish.equity.product.dto.ProductGroupRes4Hotel;
import com.yofish.equity.product.dto.ProductGroupSupplier4Filter;
import com.yofish.equity.product.enums.AttachmentType;
import com.yofish.equity.product.enums.ProductResultCodeEnum;
import com.yofish.equity.product.enums.PublishStatus;
import com.yofish.equity.product.enums.StarLevel;
import com.yofish.equity.product.enums.SupplierOrigin;
import com.youyu.common.api.PageData;
import com.youyu.common.enums.IsDeleted;
import com.youyu.common.exception.BizException;
import com.youyu.common.transfer.BaseBeanUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.PostConstruct;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductGroupServiceImpl implements ProductGroupService {

    @Autowired
    private ProductGroupRepository productGroupRepository;

    @Autowired
    private ProductGroup4HotelRepository productGroup4HotelRepository;

    @Autowired
    private ProductGroupAttachmentRepository productGroupAttachmentRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private ProductGroupSupplierRepository productGroupSupplierRepository;

    @Autowired
    private ThirdPartyService thirdPartyService;

    @Autowired
    private ProductGroup4TagRepository productGroup4TagRepository;

    @Autowired
    private ProductGroup4Hotel4FacilityRepository productGroup4Hotel4FacilityRepository;

    @Autowired
    private ProductGroup4Hotel4PolicyRepository productGroup4Hotel4PolicyRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private DestinationStrategyRepository destinationStrategyRepository;

    @Autowired
    private ProductGroup4DestinationStrategyRepository productGroup4DestinationStrategyRepository;

    @Autowired
    private ProductGroup4Hotel4FacilityService productGroup4Hotel4FacilityService;

    @Autowired
    private ProductGroupSupplierService productGroupSupplierService;

    @Autowired
    private ProductGroupAttachmentService productGroupAttachmentService;

    private Map<String, ProductGroupSupplier> productGroupSupplierMap = null;

    private List<String> hotelIdList = null;

    @PostConstruct
    public void init(){
        List<ProductGroupSupplier> productGroupSupplierList = productGroupSupplierRepository.findAll();
        hotelIdList = productGroupSupplierList.stream().filter(item -> StringUtils.isNotEmpty(item.getHotelId())).map(ProductGroupSupplier::getHotelId).collect(Collectors.toList());
        productGroupSupplierMap = productGroupSupplierList.stream().filter(item -> StringUtils.isNotEmpty(item.getHotelId())).collect(Collectors.toMap(ProductGroupSupplier::getHotelId, item -> item));
    }

    @Override
    public PageData<ProductGroup> findProductGroupPage(ProductGroup4Filter productGroup4Filter) {
        //过滤tag
        if (productGroup4Filter.getTagId() != null){
            List<Long> filterTagProductGroupIdList = filterTagProductGroup(productGroup4Filter.getTagId());
            if (CollectionUtils.isEmpty(filterTagProductGroupIdList)){
                return new PageData<>(productGroup4Filter.getPageNum(), productGroup4Filter.getPageSize());
            }
            if (CollectionUtils.isEmpty(productGroup4Filter.getProductGroupIdList())){
                productGroup4Filter.setProductGroupIdList(filterTagProductGroupIdList);
            } else {
                productGroup4Filter.getProductGroupIdList().retainAll(filterTagProductGroupIdList);
            }
        }

        //从数据库查询酒店列表
        List<ProductGroup> productGroupList = productGroupRepository.findAll(getSpecification(productGroup4Filter));
        if (CollectionUtils.isEmpty(productGroupList)){
            return PageDataHelper.toPageData(0, productGroup4Filter.getPageNum(), productGroup4Filter.getPageSize(), productGroupList);
        }

        //分页
        List<ProductGroup> finalProductGroupList = pageProductGroupList(productGroup4Filter, productGroupList);
        return PageDataHelper.toPageData(productGroupList.size(), productGroup4Filter.getPageNum(), productGroup4Filter.getPageSize(), finalProductGroupList);
    }

    @Override
    public PageData<ProductGroup4Hotel> findProductGroup4HotelPage(ProductGroup4Filter productGroup4Filter) {
        //过滤tag
        if (productGroup4Filter.getTagId() != null){
            List<Long> filterTagProductGroupIdList = filterTagProductGroup(productGroup4Filter.getTagId());
            if (CollectionUtils.isEmpty(filterTagProductGroupIdList)){
                return new PageData<>(productGroup4Filter.getPageNum(), productGroup4Filter.getPageSize());
            }
            if (CollectionUtils.isEmpty(productGroup4Filter.getProductGroupIdList())){
                productGroup4Filter.setProductGroupIdList(filterTagProductGroupIdList);
            } else {
                productGroup4Filter.getProductGroupIdList().retainAll(filterTagProductGroupIdList);
            }
        }


        //从数据库查询酒店列表
        List<ProductGroup4Hotel> productGroupList = productGroup4HotelRepository.findAll(getHotelSpecification(productGroup4Filter));
        if (CollectionUtils.isEmpty(productGroupList)){
            return PageDataHelper.toPageData(0, productGroup4Filter.getPageNum(), productGroup4Filter.getPageSize(), productGroupList);
        }


        //过滤
        List<ProductGroup4Hotel> filterProductGroupList = filterProductGroupList(productGroup4Filter, productGroupList);

        //分页
        List<ProductGroup4Hotel> finalProductGroupList = pageProductGroupList(productGroup4Filter, filterProductGroupList);
        List<Long> productGroupIdList = finalProductGroupList.stream().map(ProductGroup::getId).collect(Collectors.toList());
        //设置附件
        setAttachmentList(finalProductGroupList, productGroupIdList);


        //调用道旅api或荟集
//        callThirdPartyServiceAndSetPrice(productGroup4Filter, finalProductGroupList, productGroupIdList);

        return PageDataHelper.toPageData(filterProductGroupList.size(), productGroup4Filter.getPageNum(), productGroup4Filter.getPageSize(), finalProductGroupList);
    }

    @Override
    public PageData<ProductGroup> findProductGroup4ElectronicTicketPage(ElectronicTicketReq4Filter electronicTicketReq4Filter) {
        //过滤tag
        if (electronicTicketReq4Filter.getTagId() != null){
            List<Long> filterTagProductGroupIdList = filterTagProductGroup(electronicTicketReq4Filter.getTagId());
            if (CollectionUtils.isEmpty(filterTagProductGroupIdList)){
                return new PageData<>(electronicTicketReq4Filter.getPageNum(), electronicTicketReq4Filter.getPageSize());
            }
            if (CollectionUtils.isEmpty(electronicTicketReq4Filter.getProductGroupIdList())){
                electronicTicketReq4Filter.setProductGroupIdList(filterTagProductGroupIdList);
            } else {
                electronicTicketReq4Filter.getProductGroupIdList().retainAll(filterTagProductGroupIdList);
            }
        }

        //从数据库查询酒店列表
        List<ProductGroup> productGroupList = productGroupRepository.findAll(getSpecification(electronicTicketReq4Filter));
        if (CollectionUtils.isEmpty(productGroupList)){
            return PageDataHelper.toPageData(0, electronicTicketReq4Filter.getPageNum(), electronicTicketReq4Filter.getPageSize(), productGroupList);
        }

        //过滤
        List<ProductGroup> filterProductGroupList = filterProductGroupList(electronicTicketReq4Filter, productGroupList);
        //分页
        List<ProductGroup> finalProductGroupList = pageProductGroupList(electronicTicketReq4Filter, filterProductGroupList);

        List<Long> productGroupIdList = finalProductGroupList.stream().map(ProductGroup::getId).collect(Collectors.toList());
        //设置附件
        setAttachmentList(finalProductGroupList, productGroupIdList);


        return PageDataHelper.toPageData(filterProductGroupList.size(), electronicTicketReq4Filter.getPageNum(), electronicTicketReq4Filter.getPageSize(), finalProductGroupList);
    }

    @Override
    public List<ProductGroup> findAllByFilter(ProductGroup4Filter productGroup4Filter) {
        //从数据库查询酒店列表
        List<ProductGroup> productGroupList = productGroupRepository.findAll(getSpecification(productGroup4Filter));
        if (CollectionUtils.isEmpty(productGroupList)){
            return productGroupList;
        }
        List<Long> productGroupIdList = productGroupList.stream().map(ProductGroup::getId).collect(Collectors.toList());
        //设置附件
        setAttachmentList(productGroupList, productGroupIdList);
        return productGroupList;
    }

    private List<Long> filterTagProductGroup(Long tagId) {
        List<ProductGroup4Tag> productGroup4TagList = productGroup4TagRepository.findByTagId(tagId);
        if (CollectionUtils.isEmpty(productGroup4TagList)){
            return new ArrayList<>();
        }
        return productGroup4TagList.stream().map(productGroup4Tag -> productGroup4Tag.getProductGroup().getId()).collect(Collectors.toList());
    }

    private <T extends Object> List<T> pageProductGroupList(PageFilter pageFilter, List<T> filterProductGroupList) {
        int start = (pageFilter.getPageNum() - 1) * pageFilter.getPageSize();
        return filterProductGroupList.stream().skip(start).limit(pageFilter.getPageSize()).collect(Collectors.toList());
    }

    //TODO 放在ProductGroup类中
    private <T extends ProductGroup> void setAttachmentList(List<T> finalProductGroupList, List<Long> productGroupIdList) {
        ProductGroupAttachmentReq4Filter filter = ProductGroupAttachmentReq4Filter.builder()
                .attachmentType(AttachmentType.IMAGE)
                .productGroupIdList(productGroupIdList)
                .build();
        List<ProductGroupAttachment> attachmentList = productGroupAttachmentService.findAttachmentList(filter);
        if (CollectionUtils.isEmpty(attachmentList)){
            return;
        }
        Map<Long, List<ProductGroupAttachment>> attachmentMap = attachmentList.stream().collect(Collectors.groupingBy(item -> item.getProductGroup().getId()));
        for(ProductGroup productGroup : finalProductGroupList){
            productGroup.setProductGroupAttachmentList(attachmentMap.get(productGroup.getId()));
        }
    }

    private <T extends ProductGroup> List<T> filterProductGroupList(ProductGroup4Filter productGroup4Filter, List<T> productGroupList) {
        return productGroupList.stream().filter(item -> {
                if (item instanceof ProductGroup4Hotel){
                    ProductGroup4Hotel productGroup4Hotel = (ProductGroup4Hotel) item;
                    return filter4Hotel(productGroup4Filter, productGroup4Hotel);
                } else {
                    ProductGroup4ElectronicTicket productGroup4ElectronicTicket = (ProductGroup4ElectronicTicket) item;
                    return filter4ElectronicTicket(productGroup4Filter, productGroup4ElectronicTicket);
                }
            }).collect(Collectors.toList());
    }

    private boolean filter4ElectronicTicket(ProductGroup4Filter productGroup4Filter, ProductGroup4ElectronicTicket productGroup4ElectronicTicket) {
        return StringUtils.isEmpty(productGroup4Filter.getCityCode()) || StringUtils.equals(productGroup4Filter.getCityCode(), productGroup4ElectronicTicket.getLocation().getCityCode());
    }

    private Boolean filter4Hotel(ProductGroup4Filter productGroup4Filter, ProductGroup4Hotel productGroup4Hotel) {
        return (productGroup4Filter.getStarLevel() == null || productGroup4Hotel.getStarLevel() == productGroup4Filter.getStarLevel())
                && (StringUtils.isEmpty(productGroup4Filter.getCityCode()) || StringUtils.equals(productGroup4Filter.getCityCode(), productGroup4Hotel.getLocation().getCityCode()));
    }

    private Specification<ProductGroup4Hotel> getHotelSpecification(ProductGroup4Filter productGroup4Filter) {
        Specification<ProductGroup4Hotel> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicateList = buildPredicateList(productGroup4Filter, root, criteriaBuilder);
            if (!StringUtils.isEmpty(productGroup4Filter.getCityCode())){
                Predicate cityCode = criteriaBuilder.equal(root.get("location").get("cityCode").as(String.class), productGroup4Filter.getCityCode());
                predicateList.add(cityCode);
            }

            if (!StringUtils.isEmpty(productGroup4Filter.getTelCode())){
                Predicate telCode = criteriaBuilder.equal(root.get("location").get("telCode").as(String.class), productGroup4Filter.getTelCode());
                predicateList.add(telCode);
            }

            if (productGroup4Filter.getStarLevel() != null){
                Predicate starLevel = criteriaBuilder.equal(root.get("starLevel").as(String.class), productGroup4Filter.getStarLevel());
                predicateList.add(starLevel);
            }
            return criteriaBuilder.and(predicateList.toArray(new Predicate[predicateList.size()]));
        };
        return specification;
    }

    private Specification<ProductGroup> getSpecification(ProductGroup4Filter productGroup4Filter) {
        Specification<ProductGroup> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicateList = buildPredicateList(productGroup4Filter, root, criteriaBuilder);
            return criteriaBuilder.and(predicateList.toArray(new Predicate[predicateList.size()]));
        };
        return specification;
    }

    private <T extends ProductGroup> List<Predicate> buildPredicateList(ProductGroup4Filter productGroup4Filter, Root<T> root, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicateList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(productGroup4Filter.getProductGroupIdList())){
            predicateList.add(root.get("id").as(Long.class).in(productGroup4Filter.getProductGroupIdList()));
        }
        if (productGroup4Filter.getCategoryId() != null){
            Predicate categoryId = criteriaBuilder.equal(root.get("category").get("id").as(Long.class), productGroup4Filter.getCategoryId());
            predicateList.add(categoryId);
        }

        if (productGroup4Filter.getBrandId() != null){
            Predicate categoryId = criteriaBuilder.equal(root.get("brand").get("id").as(Long.class), productGroup4Filter.getCategoryId());
            predicateList.add(categoryId);
        }

        if (!StringUtils.isEmpty(productGroup4Filter.getProductGroupName())){
            Predicate productGroupName = criteriaBuilder.like(root.get("productGroupName").as(String.class), "%" + productGroup4Filter.getProductGroupName() + "%");
            predicateList.add(productGroupName);
        }

        if (productGroup4Filter.getPublishStatus() != null){
            Predicate publishStatus = criteriaBuilder.equal(root.get("publishStatus").as(String.class), productGroup4Filter.getPublishStatus().name());
            predicateList.add(publishStatus);
        }

        if (productGroup4Filter.getIsDeleted() != null){
            Predicate isDeleted = criteriaBuilder.equal(root.get("isDeleted").as(String.class), productGroup4Filter.getIsDeleted().name());
            predicateList.add(isDeleted);
        }

        return predicateList;
    }

    @Override
    @Transactional
    public Long createProductGroup(ProductGroupDto productGroupDto) {
        //校验分类是否存在
        Category category = categoryRepository.findById(productGroupDto.getCategoryId()).orElse(null);
        if (category == null){
            throw new BizException(ProductResultCodeEnum.CATEGORY_NOT_EXIST);
        }

        //校验标签是否存在
        List<Tag> tagList = checkAndGetTag(productGroupDto.getTagIdList());

        ProductGroup productGroup = beanConvert(productGroupDto);
        productGroup.setCategory(category);
        productGroup.setIsDeleted(IsDeleted.NOT_DELETED);
        productGroup.setPublishStatus(productGroupDto.getPublishStatus() != null ? productGroupDto.getPublishStatus() : PublishStatus.OFFLINE);
        ProductGroup hotel = productGroupRepository.save(productGroup);

        if (!CollectionUtils.isEmpty(tagList)){
            List<ProductGroup4Tag> productGroup4TagList = new ArrayList<>();
            for (Tag tag : tagList){
                ProductGroup4Tag productGroup4Tag = new ProductGroup4Tag();
                productGroup4Tag.setTag(tag);
                productGroup4Tag.setProductGroup(hotel);
                productGroup4TagList.add(productGroup4Tag);
            }
            productGroup4TagRepository.saveAll(productGroup4TagList);

        }

        //添加图片
        saveProductGroupAttachmentList(productGroupDto.getProductGroupAttachmentDtoList(), hotel);

        return hotel.getId();
    }

    @Override
    @Transactional
    public Long createProductGroup(ProductGroup productGroup) {
        //校验分类是否存在
        validateCategory(productGroup);

        List<Tag> tagList = checkAndGetTagList(productGroup.getTagList());

        ProductGroup hotel = productGroupRepository.save(productGroup);
        productGroup.setId(hotel.getId());

        saveOrUpdateProductGroupSupplier(productGroup);

        if (!CollectionUtils.isEmpty(tagList)){
            List<ProductGroup4Tag> groupProduct4TagList = productGroup.getProductGroup4TagList();
            groupProduct4TagList.forEach(item -> {
                item.setProductGroup(productGroup);
                item.setCreateTime(LocalDateTime.now());
                item.setUpdateTime(item.getCreateTime());
            });
            productGroup4TagRepository.saveAll(groupProduct4TagList);
        }

        List<ProductGroupAttachment> productGroupAttachmentList = productGroup.getProductGroupAttachmentList();
        if (!CollectionUtils.isEmpty(productGroupAttachmentList)){
            productGroupAttachmentList.forEach(item -> {
                item.setProductGroup(productGroup);
                item.setCreateTime(LocalDateTime.now());
                item.setUpdateTime(item.getCreateTime());
            });
            productGroupAttachmentRepository.saveAll(productGroupAttachmentList);
        }

        //保存目的地攻略
        List<ProductGroup4DestinationStrategy> productGroup4DestinationStrategyList = productGroup.getProductGroup4DestinationStrategyList();
        if (!CollectionUtils.isEmpty(productGroup4DestinationStrategyList)){
            productGroup4DestinationStrategyList.forEach(item -> {
                item.setProductGroup(productGroup);
                item.setCreateTime(LocalDateTime.now());
                item.setUpdateTime(item.getCreateTime());
            });
            productGroup4DestinationStrategyRepository.saveAll(productGroup4DestinationStrategyList);
        }
        return hotel.getId();
    }

    private void validateCategory(ProductGroup productGroup) {
        Category category = categoryRepository.findById(productGroup.getCategory().getId()).orElse(null);
        if (category == null){
            throw new BizException(ProductResultCodeEnum.CATEGORY_NOT_EXIST);
        }
    }

    @Override
//    @Transactional
    public int updateProductGroup(ProductGroup productGroup) {
        ProductGroup queryProductGroup = productGroupRepository.findById(productGroup.getId()).orElseGet(null);
        if (queryProductGroup == null){
            throw new BizException(ProductResultCodeEnum.HOTEL_NOT_EXIST);
        }

        //校验分类是否存在
        validateCategory(productGroup);

        productGroupRepository.save(productGroup);

//        saveOrUpdateProductGroupSupplier(productGroup);

        ProductGroupSupplier4Filter filter = ProductGroupSupplier4Filter.builder()
                .productGroupId(productGroup.getId())
                .build();
        List<ProductGroupSupplier> productGroupSupplierList = productGroupSupplierService.findListByFilter(filter);
        if (!CollectionUtils.isEmpty(productGroupSupplierList) && productGroup.getProductGroupSupplier() != null){
            ProductGroupSupplier groupSupplier = productGroupSupplierList.get(0);
            Supplier supplier = supplierRepository.findById(productGroup.getProductGroupSupplier().getSupplier().getId()).orElse(null);
            groupSupplier.setSupplier(supplier);
            SupplierOrigin supplierOrigin = supplier.getSupplierOrigin();
            if(SupplierOrigin.SELF_SIGN == supplierOrigin){
                productGroupSupplierRepository.save(groupSupplier);
            }
        }


        //更新标签
        updateProductGroupTag(productGroup);

        //更新图片,先将所有图片的序号置999
        productGroupAttachmentRepository.updateByProductGroupId(productGroup.getId());
        List<ProductGroupAttachment> productGroupAttachmentList = productGroup.getProductGroupAttachmentList();
        if (!CollectionUtils.isEmpty(productGroupAttachmentList)){
            productGroupAttachmentList.forEach(item -> {
                item.setProductGroup(productGroup);
                item.setCreateTime(LocalDateTime.now());
                item.setUpdateTime(item.getCreateTime());
            });
            productGroupAttachmentRepository.saveAll(productGroupAttachmentList);
        }

        //更新目的地攻略
        productGroup4DestinationStrategyRepository.deleteByProductGroupId(productGroup.getId());
        List<ProductGroup4DestinationStrategy> productGroup4DestinationStrategyList = productGroup.getProductGroup4DestinationStrategyList();
        if (!CollectionUtils.isEmpty(productGroup4DestinationStrategyList)){
            productGroup4DestinationStrategyList.forEach(item -> {
                item.setProductGroup(productGroup);
                item.setCreateTime(LocalDateTime.now());
                item.setUpdateTime(item.getCreateTime());
            });
            productGroup4DestinationStrategyRepository.saveAll(productGroup4DestinationStrategyList);
        }

        return 0;
    }

    private void saveOrUpdateProductGroupSupplier(ProductGroup productGroup) {
        ProductGroupSupplier productGroupSupplier = productGroup.getProductGroupSupplier();
        if (productGroupSupplier != null){
            Supplier supplier = supplierRepository.findById(productGroupSupplier.getSupplier().getId()).orElse(null);
            if (supplier == null){
                throw new BizException(ProductResultCodeEnum.SUPPLIER_NOT_EXIST);
            }
            SupplierOrigin supplierOrigin = supplier.getSupplierOrigin();
            if (supplierOrigin != SupplierOrigin.SELF_SIGN){
                return;
            }
            productGroupSupplier.setProductGroup(productGroup);
            productGroupSupplierRepository.save(productGroupSupplier);
        }
    }

    /*@Override
    @Transactional
    public int updateProductGroup(ProductGroupDto productGroupDto) {
        ProductGroup productGroup = productGroupRepository.findById(productGroupDto.getId()).orElseGet(null);
        if (productGroup == null){
            throw new BizException(ProductResultCodeEnum.HOTEL_NOT_EXIST);
        }

        //校验分类是否存在
        Category category = categoryRepository.findById(productGroupDto.getCategoryId()).orElse(null);
        if (category == null){
            throw new BizException(ProductResultCodeEnum.CATEGORY_NOT_EXIST);
        }

        ProductGroup4Hotel productGroup4Hotel = BaseBeanUtils.copy(productGroupDto, ProductGroup4Hotel.class);
        productGroup4Hotel.setCategory(category);
        productGroupRepository.save(productGroup4Hotel);

        //更新标签
        updateProductGroupTag(productGroupDto, productGroup);

        //更新图片,先将所有图片的序号置999
        productGroupAttachmentRepository.updateByProductGroupId(productGroupDto.getId());
        saveProductGroupAttachmentList(productGroupDto.getProductGroupAttachmentDtoList(), productGroup);

        //更新目的地攻略
        if (!CollectionUtils.isEmpty(productGroupDto.getDestinationStrategyIdList())){
            productGroup4DestinationStrategyRepository.deleteByProductGroupId(productGroup.getId());
            List<ProductGroup4DestinationStrategy> group4DestinationStrategyList = new ArrayList<>();
            for (Long destinationStrategyId : productGroupDto.getDestinationStrategyIdList()){
                DestinationStrategy destinationStrategy = new DestinationStrategy();
                destinationStrategy.setId(destinationStrategyId);
                ProductGroup4DestinationStrategy productGroup4DestinationStrategy = new ProductGroup4DestinationStrategy();
                productGroup4DestinationStrategy.setProductGroup(productGroup);
                productGroup4DestinationStrategy.setDestinationStrategy(destinationStrategy);
                productGroup4DestinationStrategy.setCreateTime(LocalDateTime.now());
                productGroup4DestinationStrategy.setUpdateTime(productGroup4DestinationStrategy.getCreateTime());
                group4DestinationStrategyList.add(productGroup4DestinationStrategy);
            }
            productGroup4DestinationStrategyRepository.saveAll(group4DestinationStrategyList);
//            productGroup4DestinationStrategyRepository.flush();
        }
        return 0;
    }*/

    private void saveProductGroupAttachmentList(List<ProductGroupAttachmentDto> productGroupAttachmentDtoList, ProductGroup productGroup) {
        List<ProductGroupAttachment> productGroupAttachmentList = new ArrayList<>();
        for (ProductGroupAttachmentDto productGroupAttachmentDto : productGroupAttachmentDtoList){
            ProductGroupAttachment productGroupAttachment = BaseBeanUtils.copy(productGroupAttachmentDto, ProductGroupAttachment.class);
            productGroupAttachment.setProductGroup(productGroup);
            productGroupAttachment.setDefaultImage(Boolean.TRUE);
            productGroupAttachmentList.add(productGroupAttachment);
        }
        productGroupAttachmentRepository.saveAll(productGroupAttachmentList);
//        productGroupAttachmentRepository.flush();
    }

    private void updateProductGroupTag(ProductGroup productGroup) {
        productGroup4TagRepository.deleteByProductGroupId(productGroup.getId());
        List<Tag> tagList = checkAndGetTagList(productGroup.getTagList());
        if (!CollectionUtils.isEmpty(tagList)){
            List<ProductGroup4Tag> groupProduct4TagList = productGroup.getProductGroup4TagList();
            groupProduct4TagList.forEach(item -> {
                item.setProductGroup(productGroup);
                item.setCreateTime(LocalDateTime.now());
                item.setUpdateTime(item.getCreateTime());
            });
            productGroup4TagRepository.saveAll(groupProduct4TagList);
        }
    }

    private List<Tag> checkAndGetTagList(List<Tag> tags) {
        if (CollectionUtils.isEmpty(tags)){
            return null;
        }
        List<Long> tagIdList = tags.stream().map(Tag::getId).collect(Collectors.toList());
        List<Tag> tagList = tagRepository.findAllById(tagIdList);
        if (tagList.size() != tagIdList.size()){
            throw new BizException(ProductResultCodeEnum.TAG_NOT_EXIST);
        }
        return tagList;
    }

    private List<Tag> checkAndGetTag(List<Long> tagIdList) {
        if (CollectionUtils.isEmpty(tagIdList)){
            return null;
        }

        List<Tag> tagList = tagRepository.findAllById(tagIdList);
        if (tagList.size() != tagIdList.size()){
            throw new BizException(ProductResultCodeEnum.TAG_NOT_EXIST);
        }
        return tagList;
    }

    private ProductGroup beanConvert(ProductGroupDto productGroupDto) {
        if (productGroupDto instanceof ProductGroup4HotelDto){
            return BaseBeanUtils.copy(productGroupDto, ProductGroup4Hotel.class);
        } else if (productGroupDto instanceof ProductGroup4ElectronicTicketDto){
            return BaseBeanUtils.copy(productGroupDto, ProductGroup4ElectronicTicket.class);
        } else {
            throw new BizException(ProductResultCodeEnum.CLASS_TYPE_NOT_EXIST);
        }
    }

    private ProductGroupDto beanConvert(ProductGroup productGroup) {
        if (productGroup instanceof ProductGroup4Hotel){
            return BaseBeanUtils.copy(productGroup, ProductGroup4HotelDto.class);
        } else if (productGroup instanceof ProductGroup4ElectronicTicket){
            return BaseBeanUtils.copy(productGroup, ProductGroup4ElectronicTicketDto.class);
        } else {
            throw new BizException(ProductResultCodeEnum.CLASS_TYPE_NOT_EXIST);
        }
    }

    @Override
    public <T extends ProductGroup> int batchCreateProductGroup(List<T> productGroupList) {
        productGroupRepository.saveAll(productGroupList);
        return 0;
    }

    @Override
    public void deleteProductGroup(Long id) {
        ProductGroup productGroup = productGroupRepository.findById(id).orElse(null);
        if (productGroup == null){
            throw new BizException(ProductResultCodeEnum.HOTEL_NOT_EXIST);
        }
        productGroup.setIsDeleted(IsDeleted.DELETED);
        productGroup.setCreateTime(LocalDateTime.now());
        productGroup.setUpdateTime(productGroup.getCreateTime());
        //TODO 删除触发的操作放对象中
        productGroupRepository.saveAndFlush(productGroup);
    }

    @Override
    public ProductGroupDto findById(Long id) {
        ProductGroup productGroup = productGroupRepository.findById(id).orElse(null);
        if (productGroup == null){
            throw new BizException(ProductResultCodeEnum.HOTEL_NOT_EXIST);
        }
        return beanConvert(productGroup);
    }

    @Override
    public List<ProductGroup4HotelDto> getHotelList() {
        productGroupRepository.findAll();
        return null;
    }

    @Override
    @Transactional
    public void importHotelList(List<DidaHotelDto> didaHotelDtoList) {
        List<ProductGroup> productGroups = productGroupRepository.findAllByIdGreaterThan(200L);
        List<Long> productGroupIdList = productGroups.stream().map(ProductGroup::getId).collect(Collectors.toList());
        List<ProductGroupSupplier> productGroupSupplierList = productGroupSupplierRepository.findBySupplierIdAndProductGroupIdIn(3l, productGroupIdList);
        Map<String, Long> productGroupMap = productGroupSupplierList.stream().collect(Collectors.toMap(ProductGroupSupplier::getHotelId, item -> item.getProductGroup().getId()));
        //TODO 是否加个分类类型
        Category category = categoryRepository.findById(1L).orElse(null);
        List<ProductGroup4Hotel> productGroup4HotelList = new ArrayList<>();
        for (DidaHotelDto didaHotelDto : didaHotelDtoList){
            Long id = productGroupMap.get(didaHotelDto.getHotelId());
            if (id == null){
                continue;
            }
            ProductGroup4Hotel productGroup4Hotel = BaseBeanUtils.copy(didaHotelDto, ProductGroup4Hotel.class);
            productGroup4Hotel.setId(id);
            productGroup4Hotel.setIsDeleted(IsDeleted.NOT_DELETED);
            productGroup4Hotel.setPublishStatus(PublishStatus.OFFLINE);
            productGroup4Hotel.setCreateTime(LocalDateTime.now());
            productGroup4Hotel.setUpdateTime(LocalDateTime.now());
            productGroup4Hotel.setCategory(category);
            productGroup4Hotel.setStarLevel(resolveStarLevel(didaHotelDto.getStarRating()));
            Location location = BaseBeanUtils.copy(didaHotelDto, Location.class);
            productGroup4Hotel.setLocation(location);
            productGroup4HotelList.add(productGroup4Hotel);
        }

        List<ProductGroup4Hotel> productGroup4Hotels = productGroupRepository.saveAll(productGroup4HotelList);

        /*Supplier supplier = supplierRepository.findBySupplierOrigin(SupplierOrigin.DIDA);
        List<ProductGroupSupplier> productGroupSuppliers = new ArrayList<>();
        for (ProductGroup4Hotel productGroup4Hotel : productGroup4Hotels){
            ProductGroupSupplier productGroupSupplier = new ProductGroupSupplier();
            productGroupSupplier.setHotelId(productGroup4Hotel.getHotelId());
            productGroupSupplier.setProductGroup(productGroup4Hotel);
            productGroupSupplier.setSupplier(supplier);
            productGroupSuppliers.add(productGroupSupplier);
        }
        productGroupSupplierRepository.saveAll(productGroupSuppliers);*/
    }

    @Override
    public void importHotelIdList(List<DidaHotelIdDto> didaHotelDtoList) {
        List<DidaHotelIdDto> filterHotelIdDtoList = didaHotelDtoList.stream().filter(item -> !StringUtils.isEmpty(item.getHotelId()) && !item.getHotelId().startsWith("#")).collect(Collectors.toList());

        //TODO 是否加个分类类型
        Category category = categoryRepository.findById(1L).orElse(null);
        List<ProductGroup4Hotel> productGroup4HotelList = new ArrayList<>();
        for (DidaHotelIdDto didaHotelDto : filterHotelIdDtoList){
            ProductGroup4Hotel productGroup4Hotel = BaseBeanUtils.copy(didaHotelDto, ProductGroup4Hotel.class);
            productGroup4Hotel.setIsDeleted(IsDeleted.NOT_DELETED);
            productGroup4Hotel.setPublishStatus(PublishStatus.OFFLINE);
            productGroup4Hotel.setCreateTime(LocalDateTime.now());
            productGroup4Hotel.setUpdateTime(LocalDateTime.now());
            productGroup4Hotel.setCategory(category);
            productGroup4HotelList.add(productGroup4Hotel);
        }

        List<ProductGroup4Hotel> productGroup4Hotels = productGroupRepository.saveAll(productGroup4HotelList);

//        Supplier supplier = supplierRepository.findBySupplierOrigin(SupplierOrigin.DIDA);
        Supplier supplier = null;
        List<ProductGroupSupplier> productGroupSuppliers = new ArrayList<>();
        for (ProductGroup4Hotel productGroup4Hotel : productGroup4Hotels){
            ProductGroupSupplier productGroupSupplier = new ProductGroupSupplier();
            productGroupSupplier.setHotelId(productGroup4Hotel.getHotelId());
            productGroupSupplier.setProductGroup(productGroup4Hotel);
            productGroupSupplier.setSupplier(supplier);
            productGroupSuppliers.add(productGroupSupplier);
        }
        productGroupSupplierRepository.saveAll(productGroupSuppliers);
    }



    @Override
    public void importHotelDescription(List<DidaHotelDescriptionDto> didaHotelDescriptionDtoList) {
        List<String> hotelIdList = new ArrayList<>();
        List<DidaHotelDescriptionDto> finalDidaHotelDescriptionDtoList = didaHotelDescriptionDtoList.stream().
                filter(item -> hotelIdList.contains(item.getHotelId())).collect(Collectors.toList());
        didaHotelDescriptionDtoList.clear();

        List<String> othereOtelIdList = finalDidaHotelDescriptionDtoList.stream().map(DidaHotelDescriptionDto::getHotelId).collect(Collectors.toList());
        List<ProductGroupSupplier> productGroupSupplierList = productGroupSupplierRepository.findAllByHotelIdIn(othereOtelIdList);
        Map<String, ProductGroupSupplier> productGroupSupplierMap = productGroupSupplierList.stream().collect(Collectors.toMap(ProductGroupSupplier::getHotelId, item -> item));
        List<ProductGroup4Hotel> productGroup4Hotels = new ArrayList<>();
        for (DidaHotelDescriptionDto didaHotelDescriptionDto : finalDidaHotelDescriptionDtoList){
            ProductGroupSupplier productGroupSupplier = productGroupSupplierMap.get(didaHotelDescriptionDto.getHotelId());
            if (productGroupSupplier != null){
                ProductGroup productGroup = productGroupSupplier.getProductGroup();
                if (productGroup instanceof ProductGroup4Hotel){
                    productGroup.setDescription(didaHotelDescriptionDto.getHotelDescription());
                    productGroup.setDescriptionEn(didaHotelDescriptionDto.getHotelDescriptionEN());
                    productGroup4Hotels.add((ProductGroup4Hotel)productGroup);
                }
            }
        }
        productGroupRepository.saveAll(productGroup4Hotels);
    }

    @Override
    public void importHotelAreaAttractions(List<DidaHotelAreaAttractionsDto> didaHotelAreaAttractionsDtoList) {
        List<String> hotelIdList = new ArrayList<>();
        List<DidaHotelAreaAttractionsDto> finalDidaHotelAreaAttractionsDtoList = didaHotelAreaAttractionsDtoList.stream().
                filter(item -> hotelIdList.contains(item.getHotelId())).collect(Collectors.toList());
        didaHotelAreaAttractionsDtoList.clear();

        List<String> otherHotelIdList = finalDidaHotelAreaAttractionsDtoList.stream().map(DidaHotelAreaAttractionsDto::getHotelId).collect(Collectors.toList());
        List<ProductGroupSupplier> productGroupSupplierList = productGroupSupplierRepository.findAllByHotelIdIn(otherHotelIdList);
        Map<String, ProductGroupSupplier> productGroupSupplierMap = productGroupSupplierList.stream().collect(Collectors.toMap(ProductGroupSupplier::getHotelId, item -> item));
        List<ProductGroup4Hotel> productGroup4Hotels = new ArrayList<>();
        for (DidaHotelAreaAttractionsDto didaHotelAreaAttractionsDto : finalDidaHotelAreaAttractionsDtoList){
            ProductGroupSupplier productGroupSupplier = productGroupSupplierMap.get(didaHotelAreaAttractionsDto.getHotelId());
            if (productGroupSupplier != null){
                ProductGroup productGroup = productGroupSupplier.getProductGroup();
                if (productGroup instanceof ProductGroup4Hotel){
                    ProductGroup4Hotel productGroup4Hotel = (ProductGroup4Hotel) productGroup;
                    productGroup4Hotel.setAreaAttractions(didaHotelAreaAttractionsDto.getAreaAttractions());
                    productGroup4Hotel.setAreaAttractionsEn(didaHotelAreaAttractionsDto.getAreaAttractionsEn());
                    productGroup4Hotels.add((ProductGroup4Hotel)productGroup);
                }
            }
        }
        productGroupRepository.saveAll(productGroup4Hotels);
    }

    @Override
    public void importHotelFacility(List<DidaHotelFacilityDto> didaHotelFacilityDtoList) {
        List<String> hotelIdList = new ArrayList<>();
        List<DidaHotelFacilityDto> finalDidaHotelFacilityDtoList = didaHotelFacilityDtoList.stream().
                filter(item -> hotelIdList.contains(item.getHotelId())).collect(Collectors.toList());
        didaHotelFacilityDtoList.clear();

        List<String> otherHotelIdList = finalDidaHotelFacilityDtoList.stream().map(DidaHotelFacilityDto::getHotelId).collect(Collectors.toList());
        List<ProductGroupSupplier> productGroupSupplierList = productGroupSupplierRepository.findAllByHotelIdIn(otherHotelIdList);
        Map<String, ProductGroupSupplier> productGroupSupplierMap = productGroupSupplierList.stream().collect(Collectors.toMap(ProductGroupSupplier::getHotelId, item -> item));
        List<ProductGroup4Hotel4Facility> productGroup4Hotel4Facilities = new ArrayList<>();
        for (DidaHotelFacilityDto didaHotelFacilityDto : finalDidaHotelFacilityDtoList){
            ProductGroupSupplier productGroupSupplier = productGroupSupplierMap.get(didaHotelFacilityDto.getHotelId());
            if (productGroupSupplier != null){
                ProductGroup productGroup = productGroupSupplier.getProductGroup();
                if (productGroup instanceof ProductGroup4Hotel){
                    ProductGroup4Hotel productGroup4Hotel = (ProductGroup4Hotel) productGroup;
                    ProductGroup4Hotel4Facility productGroup4Hotel4Facility = new ProductGroup4Hotel4Facility();
                    productGroup4Hotel4Facility.setProductGroup4Hotel(productGroup4Hotel);
                    productGroup4Hotel4Facility.setFacilityType(didaHotelFacilityDto.getFacilityType());
                    productGroup4Hotel4Facility.setDescription(didaHotelFacilityDto.getDescription());
                    productGroup4Hotel4Facility.setDescriptionEn(didaHotelFacilityDto.getDescriptionEn());
                    productGroup4Hotel4Facility.setCreateTime(LocalDateTime.now());
                    productGroup4Hotel4Facility.setUpdateTime(productGroup4Hotel4Facility.getCreateTime());
                    productGroup4Hotel4Facilities.add(productGroup4Hotel4Facility);
                }
            }
        }
        productGroup4Hotel4FacilityRepository.saveAll(productGroup4Hotel4Facilities);
    }

    /*@Override
    public void updateHotelFacility(List<DidaHotelFacilityDto> didaHotelFacilityDtoList) {
        List<DidaHotelFacilityDto> finalDidaHotelFacilityDtoList = didaHotelFacilityDtoList.stream().
                filter(item -> hotelIdList.contains(item.getHotelId())).collect(Collectors.toList());
        didaHotelFacilityDtoList.clear();

        List<String> hotelIdList = finalDidaHotelFacilityDtoList.stream().map(DidaHotelFacilityDto::getHotelId).collect(Collectors.toList());
        List<ProductGroupSupplier> productGroupSupplierList = productGroupSupplierRepository.findAllByHotelIdIn(hotelIdList);
        List<Long> productGroupIdList = productGroupSupplierList.stream().map(item -> item.getProductGroup().getId()).distinct().collect(Collectors.toList());
        ProductGroup4Hotel4FacilityReq4Filter filter = ProductGroup4Hotel4FacilityReq4Filter.builder()
                .productGroupIdList(productGroupIdList).build();
        List<ProductGroup4Hotel4FacilityDto> productGroup4Hotel4FacilityDtoList = productGroup4Hotel4FacilityService.findFacilityList(filter);
        productGroup4Hotel4FacilityDtoList.forEach(item -> {

        });

        Map<String, ProductGroupSupplier> productGroupSupplierMap = productGroupSupplierList.stream().collect(Collectors.toMap(ProductGroupSupplier::getHotelId, item -> item));
        List<ProductGroup4Hotel4Facility> productGroup4Hotel4Facilities = new ArrayList<>();
        for (DidaHotelFacilityDto didaHotelFacilityDto : finalDidaHotelFacilityDtoList){
            ProductGroupSupplier productGroupSupplier = productGroupSupplierMap.get(didaHotelFacilityDto.getHotelId());
            if (productGroupSupplier != null){
                ProductGroup productGroup = productGroupSupplier.getProductGroup();
                if (productGroup instanceof ProductGroup4Hotel){
                    ProductGroup4Hotel productGroup4Hotel = (ProductGroup4Hotel) productGroup;
                    ProductGroup4Hotel4Facility productGroup4Hotel4Facility = new ProductGroup4Hotel4Facility();
                    productGroup4Hotel4Facility.setProductGroup4Hotel(productGroup4Hotel);
                    productGroup4Hotel4Facility.setFacilityType(didaHotelFacilityDto.getFacilityType());
                    productGroup4Hotel4Facility.setDescription(didaHotelFacilityDto.getDescription());
                    productGroup4Hotel4Facility.setDescriptionEn(didaHotelFacilityDto.getDescriptionEn());
                    productGroup4Hotel4Facility.setCreateTime(LocalDateTime.now());
                    productGroup4Hotel4Facility.setUpdateTime(productGroup4Hotel4Facility.getCreateTime());
                    productGroup4Hotel4Facilities.add(productGroup4Hotel4Facility);
                }
            }
        }
        productGroup4Hotel4FacilityRepository.saveAll(productGroup4Hotel4Facilities);
    }*/

    @Transactional
    @Override
    public void importHotelPolicy(List<DidaHotelPolicyDto> didaHotelPolicyDtoList) {
        List<DidaHotelPolicyDto> finalDidaHotelPolicyDtoList = didaHotelPolicyDtoList.stream().
                filter(item -> hotelIdList.contains(item.getHotelId())).collect(Collectors.toList());
        didaHotelPolicyDtoList.clear();

        List<String> otherHotelIdList = finalDidaHotelPolicyDtoList.stream().map(DidaHotelPolicyDto::getHotelId).collect(Collectors.toList());
        List<ProductGroupSupplier> productGroupSupplierList = productGroupSupplierRepository.findAllByHotelIdIn(otherHotelIdList);
        Map<String, ProductGroupSupplier> productGroupSupplierMap = productGroupSupplierList.stream().collect(Collectors.toMap(ProductGroupSupplier::getHotelId, item -> item));
        List<ProductGroup4Hotel4Policy> productGroup4Hotel4Policies = new ArrayList<>();
        for (DidaHotelPolicyDto didaHotelPolicyDto : finalDidaHotelPolicyDtoList){
            ProductGroupSupplier productGroupSupplier = productGroupSupplierMap.get(didaHotelPolicyDto.getHotelId());
            if (productGroupSupplier != null){
                ProductGroup productGroup = productGroupSupplier.getProductGroup();
                if (productGroup instanceof ProductGroup4Hotel){
                    ProductGroup4Hotel productGroup4Hotel = (ProductGroup4Hotel) productGroup;
                    ProductGroup4Hotel4Policy productGroup4Hotel4Policy = new ProductGroup4Hotel4Policy();
                    productGroup4Hotel4Policy.setProductGroup4Hotel(productGroup4Hotel);
                    productGroup4Hotel4Policy.setPolicyType(didaHotelPolicyDto.getPolicyType());
                    productGroup4Hotel4Policy.setDescription(didaHotelPolicyDto.getDescription());
                    productGroup4Hotel4Policy.setDescriptionEn(didaHotelPolicyDto.getDescriptionEn());
                    productGroup4Hotel4Policy.setCreateTime(LocalDateTime.now());
                    productGroup4Hotel4Policy.setUpdateTime(productGroup4Hotel4Policy.getCreateTime());
                    productGroup4Hotel4Policies.add(productGroup4Hotel4Policy);
                }
            }
        }

         if (!CollectionUtils.isEmpty(productGroup4Hotel4Policies)){
            //删除原来的数据
            List<Long> productGroupIdList = productGroup4Hotel4Policies.stream().map(productGroup4Hotel4Policy -> productGroup4Hotel4Policy.getProductGroup4Hotel().getId())
                    .collect(Collectors.toList());
            productGroup4Hotel4PolicyRepository.deleteByProductGroup4HotelIdList(productGroupIdList);
            //保存新的数据
            productGroup4Hotel4PolicyRepository.saveAll(productGroup4Hotel4Policies);
        }

    }

    @Override
    public void updateProductPublishStatus(ProductGroupReq4PublishStatus productGroupReq4PublishStatus) {
        ProductGroup productGroup = BaseBeanUtils.copy(productGroupReq4PublishStatus, ProductGroup.class);
        productGroupRepository.updateProductGroup(productGroup);
    }

    @Override
    public int importHotelImageList(List<DidaHotelImageDto> didaHotelImageDtoList) {
        log.info("原始size:{},data:{}", hotelIdList.size(), hotelIdList);
        List<DidaHotelImageDto> didaHotelImageDtos = new ArrayList<>();
        for (DidaHotelImageDto didaHotelImageDto : didaHotelImageDtoList){
            if (hotelIdList.contains(didaHotelImageDto.getHotelId())){
                didaHotelImageDtos.add(didaHotelImageDto);
            }
        }
        didaHotelImageDtoList.clear();
        if (CollectionUtils.isEmpty(didaHotelImageDtos)){
            return 0;
        }

        List<ProductGroupAttachment> productGroupAttachmentList = new ArrayList<>();
        List<String> hotelIdStringList = new ArrayList<>();
        for (DidaHotelImageDto didaHotelImageDto : didaHotelImageDtos){
            hotelIdStringList.add(didaHotelImageDto.getHotelId());
            ProductGroupSupplier productGroupSupplier = productGroupSupplierMap.get(didaHotelImageDto.getHotelId());
            if (productGroupSupplier != null){
                ProductGroupAttachment productGroupAttachment = new ProductGroupAttachment();
                productGroupAttachment.setAttachmentName(didaHotelImageDto.getImageCaption());
                productGroupAttachment.setAttachmentType(AttachmentType.IMAGE);
                productGroupAttachment.setAttachmentUrl(didaHotelImageDto.getImageUrl());
                productGroupAttachment.setSortNum(Integer.valueOf(didaHotelImageDto.getImageOrder()));
                productGroupAttachment.setProductGroup(productGroupSupplier.getProductGroup());
                productGroupAttachmentList.add(productGroupAttachment);
            }
        }
        List<String> filter = hotelIdStringList.stream().distinct().collect(Collectors.toList());
        log.info("size:{},data:{}", filter.size(), filter);
        productGroupAttachmentRepository.saveAll(productGroupAttachmentList);
        return filter.size();
    }

    @Override
    public List<ProductGroup> findAllProductGroupList() {
        List<ProductGroup> productGroupList = productGroupRepository.findByCategoryId(1L);
        List<Long> productGroupIdList = productGroupList.stream().map(ProductGroup::getId).collect(Collectors.toList());
        setAttachmentList(productGroupList, productGroupIdList);
        return productGroupList;
    }

    @Override
    public ProductGroupRes4Hotel findProductHotelById(Long groupId) {
        ProductGroup4Hotel productGroup4Hotel = productGroup4HotelRepository.findById(groupId).get();
        if (ObjectUtils.isEmpty(productGroup4Hotel)){
            return null;
        }
        return BaseBeanUtils.copy(productGroup4Hotel, ProductGroupRes4Hotel.class);
    }

    private StarLevel resolveStarLevel(String starRating) {
        if (StringUtils.isEmpty(starRating)){
            return null;
        }
        BigDecimal bigDecimal = new BigDecimal(starRating);
        if (bigDecimal.compareTo(new BigDecimal(4)) < 0){
            return StarLevel.THREE_STAR;
        } else if (bigDecimal.compareTo(new BigDecimal(5)) < 0){
            return StarLevel.FOUR_STAR;
        } else {
            return StarLevel.FIVE_STAR;
        }
    }


}
