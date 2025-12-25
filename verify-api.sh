#!/bin/bash

# API Compatibility Verification Script
# Verifies that all public API classes are accessible after refactoring

echo "============================================================"
echo "API Compatibility Verification"
echo "============================================================"
echo ""

# Color codes
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

total=0
found=0
missing=0

check_file() {
    local class_name=$1
    local file_path=$2
    
    total=$((total + 1))
    
    if [ -f "$file_path" ]; then
        echo -e "${GREEN}✓${NC} $class_name - Found"
        found=$((found + 1))
        return 0
    else
        echo -e "${RED}✗${NC} $class_name - MISSING at $file_path"
        missing=$((missing + 1))
        return 1
    fi
}

echo "Checking Lifecycle Package (was app/)..."
check_file "BaseApplication" "swallow/src/main/java/com/swallow/fly/base/lifecycle/BaseApplication.kt"
check_file "AppDelegate" "swallow/src/main/java/com/swallow/fly/base/lifecycle/AppDelegate.kt"
check_file "AppLifecycles" "swallow/src/main/java/com/swallow/fly/base/lifecycle/AppLifecycles.kt"
check_file "AppModule" "swallow/src/main/java/com/swallow/fly/base/lifecycle/AppModule.kt"
check_file "ConfigModule" "swallow/src/main/java/com/swallow/fly/base/lifecycle/ConfigModule.kt"
echo ""

echo "Checking UI Package (was view/)..."
check_file "IActivity" "swallow/src/main/java/com/swallow/fly/base/ui/activity/IActivity.kt"
check_file "BaseActivity" "swallow/src/main/java/com/swallow/fly/base/ui/activity/BaseActivity.kt"
check_file "FastBaseActivity" "swallow/src/main/java/com/swallow/fly/base/ui/activity/FastBaseActivity.kt"
check_file "IFragment" "swallow/src/main/java/com/swallow/fly/base/ui/fragment/IFragment.kt"
check_file "BaseFragment" "swallow/src/main/java/com/swallow/fly/base/ui/fragment/BaseFragment.kt"
check_file "BaseLazyFragment" "swallow/src/main/java/com/swallow/fly/base/ui/fragment/BaseLazyFragment.kt"
check_file "ViewBehavior" "swallow/src/main/java/com/swallow/fly/base/ui/ViewBehavior.kt"
echo ""

echo "Checking Presentation Package (was viewmodel/)..."
check_file "IViewModel" "swallow/src/main/java/com/swallow/fly/base/presentation/IViewModel.kt"
check_file "BaseViewModel" "swallow/src/main/java/com/swallow/fly/base/presentation/BaseViewModel.kt"
check_file "UiState" "swallow/src/main/java/com/swallow/fly/base/presentation/state/UiState.kt"
check_file "UiEvent" "swallow/src/main/java/com/swallow/fly/base/presentation/state/UiEvent.kt"
check_file "PageViewState" "swallow/src/main/java/com/swallow/fly/base/presentation/state/PageViewState.kt"
check_file "PageListState" "swallow/src/main/java/com/swallow/fly/base/presentation/state/PageListState.kt"
check_file "PageStateEvent" "swallow/src/main/java/com/swallow/fly/base/presentation/state/PageStateEvent.kt"
check_file "BaseStateEvent" "swallow/src/main/java/com/swallow/fly/base/presentation/state/BaseStateEvent.kt"
echo ""

echo "Checking Data Package (was repository/)..."
check_file "IRepository" "swallow/src/main/java/com/swallow/fly/base/data/IRepository.kt"
check_file "BaseRepository" "swallow/src/main/java/com/swallow/fly/base/data/BaseRepository.kt"
check_file "Repository" "swallow/src/main/java/com/swallow/fly/base/data/Repository.kt"
echo ""

echo "============================================================"
echo "Summary:"
echo "  Total classes checked: $total"
echo -e "  ${GREEN}Found: $found${NC}"
if [ $missing -gt 0 ]; then
    echo -e "  ${RED}Missing: $missing${NC}"
else
    echo -e "  ${GREEN}Missing: $missing${NC}"
fi
echo ""

if [ $missing -eq 0 ]; then
    echo -e "${GREEN}✓ API COMPATIBILITY CHECK PASSED${NC}"
    echo "  All public API classes are in their expected locations."
    exit 0
else
    echo -e "${RED}✗ API COMPATIBILITY CHECK FAILED${NC}"
    echo "  Some public API classes are missing or misplaced."
    exit 1
fi
