package com.atguigu.auth.service.impl;

import com.atguigu.auth.mapper.SysMenuMapper;
import com.atguigu.auth.service.SysMenuService;
import com.atguigu.auth.service.SysRoleMenuService;
import com.atguigu.auth.util.MenuHelper;
import com.atguigu.common.config.exception.GuiguException;
import com.atguigu.model.system.SysMenu;
import com.atguigu.model.system.SysRoleMenu;
import com.atguigu.vo.system.AssignMenuVo;
import com.atguigu.vo.system.MetaVo;
import com.atguigu.vo.system.RouterVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {
    @Autowired
    private SysRoleMenuService sysRoleMenuService;
    @Override
    public List<SysMenu> findNodes() {
        List<SysMenu> sysMenuList = baseMapper.selectList(null);

        //构建树形结构
        List<SysMenu> result = MenuHelper.buildTree(sysMenuList);
        return result;
    }

    @Override
    public void removeMenuById(Long id) {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getParentId, id);
        Integer count = baseMapper.selectCount(wrapper);
        if(count > 0){
            throw new GuiguException(201, "菜单不能删除");
        }
        baseMapper.deleteById(id);
    }

    @Override
    public List<SysMenu> findSysMenuByRoleId(Long roleId) {
        //1 查询所有status=1的菜单
        LambdaQueryWrapper<SysMenu> wrapperSysMenu = new LambdaQueryWrapper<>();
        wrapperSysMenu.eq(SysMenu::getStatus, 1);
        List<SysMenu> allSysMenus = baseMapper.selectList(wrapperSysMenu);

        //2 根据角色id，查询可操作的菜单id
        LambdaQueryWrapper<SysRoleMenu> wrapperSysRoleMenu = new LambdaQueryWrapper<>();
        wrapperSysRoleMenu.eq(SysRoleMenu::getRoleId, roleId);
        List<SysRoleMenu> sysRoleMenuList = sysRoleMenuService.list(wrapperSysRoleMenu);
        List<Long> operableMenuIDs = sysRoleMenuList.stream().map(c -> c.getMenuId()).collect(Collectors.toList());

        //3 根据菜单id，获取菜单对象
        //3.1 逐个将所有菜单集合中的id与可操作菜单的id比较，如果相同，则封装
        allSysMenus.stream().forEach(item->{
            if(operableMenuIDs.contains(item.getId())){
                item.setSelect(true);
            }else{
                item.setSelect(false);
            }
        });

        //返回规定格式菜单列表
        List<SysMenu> sysMenusTree = MenuHelper.buildTree(allSysMenus);
        return sysMenusTree;
    }

    @Override
    public void doAssign(AssignMenuVo assignMenuVo) {
        //删除角色原有菜单
        LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenu::getRoleId, assignMenuVo.getRoleId());
        sysRoleMenuService.remove(wrapper);

        //向角色分配新菜单
        List<Long> menuIdList = assignMenuVo.getMenuIdList();
        for(Long menuId:menuIdList){
            if (StringUtils.isEmpty(menuId)) continue;
            SysRoleMenu rolePermission = new SysRoleMenu();
            rolePermission.setRoleId(assignMenuVo.getRoleId());
            rolePermission.setMenuId(menuId);
            sysRoleMenuService.save(rolePermission);
        }
    }

    /**
     * 获取用户可操作的菜单
     * @param userId
     * @return
     */
    @Override
    public List<RouterVo> findMenusByUserID(Long userId) {
        List<SysMenu> sysMenus = null;
        //1.判断用户是否为管理员（userId=1）
        //1.1 如果是管理员，查询所有菜单
        if(userId.longValue() == 1){
            LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysMenu::getStatus, 1);
            wrapper.orderByAsc(SysMenu::getSortValue);
            sysMenus = baseMapper.selectList(wrapper);
        }else{ //1.2 如果不是管理员，根据userId查询可以操作的菜单
            sysMenus = baseMapper.findMenusByUserID(userId);
        }

        //2.将查询出的数据构建成框架要求的路由结构
        //树形结构
        List<SysMenu> sysMenuTree = MenuHelper.buildTree(sysMenus);
        //框架要求的路由结构
        List<RouterVo> routers = this.buildRouter(sysMenuTree);


        return routers;
    }

    /**
     * 构建框架要求的路由结构
     * @param menus
     * @return
     */
    private List<RouterVo> buildRouter(List<SysMenu> menus) {
        List<RouterVo> routers = new ArrayList<>();
        for(SysMenu menu: menus){
            RouterVo router = new RouterVo();
            router.setHidden(false);
            router.setAlwaysShow(false);
            router.setPath(getRouterPath(menu));
            router.setComponent(menu.getComponent());
            router.setMeta(new MetaVo(menu.getName(), menu.getIcon()));
            //下一层数据
            List<SysMenu> children = menu.getChildren();
            //如果当前是菜单，需将按钮对应的路由加载出来，如：“角色授权”按钮对应的路由在“系统管理”下面
            if(menu.getType().intValue() == 1) {
                List<SysMenu> hiddenMenuList = children.stream().filter(item -> !StringUtils.isEmpty(item.getComponent())).collect(Collectors.toList());
                for (SysMenu hiddenMenu : hiddenMenuList) {
                    RouterVo hiddenRouter = new RouterVo();
                    hiddenRouter.setHidden(true);
                    hiddenRouter.setAlwaysShow(false);
                    hiddenRouter.setPath(getRouterPath(hiddenMenu));
                    hiddenRouter.setComponent(hiddenMenu.getComponent());
                    hiddenRouter.setMeta(new MetaVo(hiddenMenu.getName(), hiddenMenu.getIcon()));
                    routers.add(hiddenRouter);
                }
            } else {
                if (!CollectionUtils.isEmpty(children)) {
                    if(children.size() > 0) {
                        router.setAlwaysShow(true);
                    }
                    router.setChildren(buildRouter(children));
                }
            }
            routers.add(router);
        }
        return routers;
    }

    /**
     * 获取路由地址
     * @param menu 菜单信息
     * @return 路由地址
     */
    public String getRouterPath(SysMenu menu) {
        String routerPath = "/" + menu.getPath();
        if(menu.getParentId().intValue() != 0) {
            routerPath = menu.getPath();
        }
        return routerPath;
    }

    /**
     * 获取用户可操作的按钮
     * @param userId
     * @return
     */
    @Override
    public List<String> findPermsByUserID(Long userId) {
        List<SysMenu> sysMenus = null;
        //1.判断用户是否为管理员（userId=1）
        //1.1 如果是管理员，查询所有按钮
        if(userId.longValue() == 1){
            LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysMenu::getStatus, 1);
            sysMenus = baseMapper.selectList(wrapper);
        }else{ //1.2 如果不是管理员，根据userId查询可以操作的按钮
            sysMenus = baseMapper.findMenusByUserID(userId);
        }

        //2.获取用户可操作的按钮
        List<String> perms = sysMenus.stream()
                .filter(item -> item.getType() == 2)
                .map(item -> item.getPerms())
                .collect(Collectors.toList());
        return perms;
    }


}
