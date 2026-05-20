<#if page?? && (page.totalPages> 1)><nav class="mt-3">
        <ul class="pagination justify-content-center">
            <li class="page-item <#if page.first>disabled</#if>"><a class="page-link"
                    href="?page=${page.number-1}&size=${page.size}">Назад</a></li>
            <#list 0..(page.totalPages-1) as i>
                <li class="page-item <#if i==page.number>active</#if>"><a class="page-link"
                        href="?page=${i}&size=${page.size}">${i+1}</a></li>
            </#list>
            <li class="page-item <#if page.last>disabled</#if>"><a class="page-link"
                    href="?page=${page.number+1}&size=${page.size}">Вперёд</a></li>
        </ul>
    </nav>
</#if>